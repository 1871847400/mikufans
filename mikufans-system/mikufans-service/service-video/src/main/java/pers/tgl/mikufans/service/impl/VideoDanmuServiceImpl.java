package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.enums.DanmuType;
import pers.tgl.mikufans.domain.enums.NoticeType;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoDanmu;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.dto.DanmuImportDto;
import pers.tgl.mikufans.dto.VideoDanmuDto;
import pers.tgl.mikufans.event.EventAction;
import pers.tgl.mikufans.event.UserReportAuditEvent;
import pers.tgl.mikufans.event.VideoDanmuEvent;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.VideoDanmuMapper;
import pers.tgl.mikufans.model.BiliDanmu;
import pers.tgl.mikufans.service.*;
import pers.tgl.mikufans.socket.DanmuSocketListener;
import pers.tgl.mikufans.util.BiliDanmuUtils;
import pers.tgl.mikufans.vo.VideoDanmuVo;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
* @author TGL
* @description 针对表【video_danmu(视频弹幕表)】的数据库操作Service实现
* @createDate 2023-01-15 10:51:58
*/
@Service
@RequiredArgsConstructor
public class VideoDanmuServiceImpl extends BaseServiceImpl<VideoDanmu, VideoDanmuMapper> implements VideoDanmuService {
    private final VideoService videoService;
    private final VideoPartService videoPartService;
    private final UserLikeDataService userLikeDataService;
    private final UserNoticeService userNoticeService;
    private final DanmuSocketListener danmuSocketListener;

    @Override
    public List<VideoDanmuVo> listByPartId(Long partId, @Nullable Long minId) {
        List<VideoDanmuVo> list = wrapper()
                .eq(VideoDanmu::getPartId, partId)
                .gtIfExists(VideoDanmu::getId, minId)
                .orderByDesc(VideoDanmu::getCreateTime)
                .last("limit 1000")
                .list(VideoDanmuVo.class);
        for (VideoDanmuVo videoDanmu : list) {
            videoDanmu.setLikeStatus(userLikeDataService.getStatus(videoDanmu.getId()));
        }
        return list;
    }

    @Override
    public VideoDanmu createVideoDanmu(VideoDanmuDto dto) {
        VideoDanmu danmu = BeanUtil.toBean(dto, VideoDanmu.class);
        VideoPart part = videoPartService.getById(dto.getPartId());
        if (part == null) {
            throw new CustomException(Code.RESOURCE_NOT_FOUND);
        }
        danmu.setVideoId(part.getVideoId());
        save(danmu);
        userLikeDataService.createLikeData(BusinessType.DANMU, danmu.getId());
        publishEvent(new VideoDanmuEvent(this, danmu, EventAction.INSERT));
        VideoDanmuVo videoDanmu = getById(danmu.getId(), VideoDanmuVo.class);
        if (videoDanmu != null) {
            videoDanmu.setLikeStatus(userLikeDataService.getStatus(videoDanmu.getId()));
        }
        return videoDanmu;
    }


    @Override
    public String importBatch(DanmuImportDto dto) {
        VideoPart part = videoPartService.getById(dto.getPartId());
        if (part == null) {
            throw new CustomException(Code.RESOURCE_NOT_FOUND);
        }
        HttpRequest request = HttpUtil.createGet(dto.getUrl());
        String body;
        try (HttpResponse response = request.execute()) {
            body = response.body();
            if (!response.isOk()) {
                throw new CustomException("获取链接内容失败: " + body);
            }
        }
        List<BiliDanmu> biliDanmus = BiliDanmuUtils.parse(body);
        List<VideoDanmu> videoDanmus = new LinkedList<>();
        for (BiliDanmu biliDanmu : biliDanmus) {
            VideoDanmu videoDanmu = new VideoDanmu();
            videoDanmu.setPartId(dto.getPartId());
            videoDanmu.setVideoId(part.getVideoId());
            videoDanmu.setUserId(dto.getUserId());
            videoDanmu.setContent(biliDanmu.getText());
            videoDanmu.setSendTime(BigDecimal.valueOf(biliDanmu.getStime()));
            videoDanmu.setFontType(1);
            videoDanmu.setFontColor("#" + HexUtil.toHex(biliDanmu.getColor()));
            if (biliDanmu.getMode() == 4) {
                videoDanmu.setDanmuType(DanmuType.FIXED_BOTTOM);
            } else if (biliDanmu.getMode() == 5) {
                videoDanmu.setDanmuType(DanmuType.FIXED_TOP);
            } else {
                videoDanmu.setDanmuType(DanmuType.ROLL);
            }
            videoDanmu.setRemark("BiliDanmu");
            videoDanmus.add(videoDanmu);
        }
        super.saveBatch(videoDanmus);
        for (VideoDanmu danmu : videoDanmus) {
            if (isValidId(danmu.getId())) {
                userLikeDataService.createLikeData(BusinessType.DANMU, danmu.getId());
                publishEvent(new VideoDanmuEvent(this, danmu, EventAction.INSERT));
            }
        }
        return String.format("成功导入 %d 条弹幕", biliDanmus.size());
    }

    @Override
    public boolean removeById(VideoDanmu entity) {
        if (super.removeById(entity)) {
            publishEvent(new VideoDanmuEvent(this, entity, EventAction.DELETE));
            return true;
        }
        return false;
    }

    @EventListener(UserReportAuditEvent.class)
    @Transactional(rollbackFor = Exception.class)
    public void listen(UserReportAuditEvent e) {
        if (e.getReportType() == BusinessType.DANMU) {
            VideoDanmu danmu = findById(e.getTargetId());
            if (danmu == null) {
                return;
            }
            String title = videoService.findById(danmu.getVideoId(), Video::getTitle);
            NoticeType noticeType = NoticeType.DANMU_REPORT_FAIL;
            if (e.getAuditStatus() == AuditStatus.SUCCESS) {
                removeById(danmu.getId());
                noticeType = NoticeType.DANMU_REPORT_SUCCESS;
                userNoticeService.createNotice(NoticeType.DANMU_VIOLATION, danmu.getUserId(), danmu.getUri(), title, danmu.getContent(), e.getMessage());
            }
            for (Long userId : e.getUserIds()) {
                userNoticeService.createNotice(noticeType, userId, danmu.getUri(), title, danmu.getContent());
            }
        }
    }
}