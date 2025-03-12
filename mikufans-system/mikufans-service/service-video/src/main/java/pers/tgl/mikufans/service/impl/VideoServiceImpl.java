package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.config.upload.UploadConfig;
import pers.tgl.mikufans.consts.FileMediaType;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.enums.AuditType;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.enums.NoticeType;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.dto.VideoDto;
import pers.tgl.mikufans.dto.VideoPartDto;
import pers.tgl.mikufans.event.*;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.VideoMapper;
import pers.tgl.mikufans.service.*;
import pers.tgl.mikufans.util.DbUtils;
import pers.tgl.mikufans.vo.VideoPartVo;
import pers.tgl.mikufans.vo.VideoVo;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author TGL
 * @description 针对表【video(视频表)】的数据库操作Service实现
 * @createDate 2022-12-31 10:17:31
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl extends BaseServiceImpl<Video, VideoMapper> implements VideoService {
    private final UserService userService;
    private final BangumiService bangumiService;
    private final AppConfig appConfig;
    private final UserStarService userStarService;
    private final VideoCoinService videoCoinService;
    private final VideoWatchHistoryService videoWatchHistoryService;
    private final UserDynamicService userDynamicService;
    private final VideoPartService videoPartService;
    private final UserNoticeService userNoticeService;
    private final SysAuditService sysAuditService;

    @Override
    public VideoVo getVoById(Object id) {
        VideoVo video = wrapper()
                .eq(Video::getId, id)
                .or().eq(Video::getSid, id)
                .one(VideoVo.class);
        if (video != null) {
            Long vid = video.getId();
            video.setUser(userService.getVoById(video.getUserId()));
            video.setUserStar(userStarService.hasStarVideo(vid));
            video.setUserCoin(videoCoinService.getUserCoin(vid));
            video.setBangumi(bangumiService.getVoById(video.getBangumiId()));
            video.setHistory(videoWatchHistoryService.getVoByVid(vid));
            video.setParts(videoPartService.getVideoParts(vid));
            video.setCanplayCount(videoPartService.countCanplay(vid, true));
            video.setDynamic(userDynamicService.getVoById(vid));
        }
        return video;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(Long videoId) {
        Video video = getById(videoId);
        checkUserPermission(video);
        if (!Objects.equals(video.getDisabled(), 1)) {
            throw new CustomException("不需要重审");
        }
        List<VideoPartVo> videoParts = videoPartService.getVideoParts(videoId);
        for (VideoPartVo videoPart : videoParts) {
            if (videoPart.getSysAudit().getAuditStatus() == AuditStatus.SUCCESS) {
                sysAuditService.setAuditStatus(videoPart.getId(), AuditStatus.UNKNOWN);
            }
        }
        updateById(videoId, Video::getDisabled, 0);
        updateById(videoId, Video::getReason, "");
        incrementById(videoId, Video::getApply, 1);
        userNoticeService.createNotice(NoticeType.VIDEO_APPLY, video.getUserId(), "", video.getTitle());
    }

    @Override
    public boolean removeById(Video entity) {
        if (super.removeById(entity)) {
            userDynamicService.removeById(entity.getId());
            publishEvent(new VideoEvent(this, entity, EventAction.DELETE));
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVideo(VideoDto dto) {
        Video video = getById(dto.getId());
        if (video == null) {
            throw new CustomException("视频不存在");
        }
        if (video.getType().isBangumi()) {
            //只有普通视频才能设置频道
            dto.setChannelId(null);
            //更新番剧信息
            if (dto.getBangumi() != null) {
                bangumiService.updateBangumi(dto.getBangumi());
            }
        }
        //观看等级最高为用户当前等级
        if (dto.getUserLevel() != null) {
            dto.setUserLevel(Math.min(userService.getUserLevel(null), dto.getUserLevel()));
        }
        if (dto.getDynamic() != null) {
            userDynamicService.updateDto(dto.getDynamic());
        }
        if (updateById(BeanUtil.toBean(dto, Video.class))) {
            publishEvent(new VideoEvent(this, getById(dto.getId()), EventAction.UPDATE));
        }
        if (dto.getCreateParts() != null) {
            for (VideoPartDto partDto : dto.getCreateParts()) {
                partDto.setVideoId(video.getId());
                partDto.setAppend(1);
                videoPartService.createVideoPart(partDto);
            }
        }
        if (dto.getUpdateParts() != null) {
            for (VideoPartDto partDto : dto.getUpdateParts()) {
                videoPartService.updateVideoPart(partDto);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long createVideo(VideoDto dto) {
        //限制创建数量
        UploadConfig uploadConfig = appConfig.getUpload().get(FileMediaType.VIDEO);
        //检查用户上传限制
        DbUtils.checkLimit(Video.class, uploadConfig.getLimit());
        //观看等级最高为用户当前等级
        if (dto.getUserLevel() != null) {
            dto.setUserLevel(Math.min(userService.getUserLevel(null), dto.getUserLevel()));
        }
        if (dto.getType().isBangumi()) {
            dto.setChannelId(0L);
        }
        Video video = BeanUtil.toBean(dto, Video.class);
        video.setBangumiId(0L);
        video.setSearch(0);
        video.setUid(wrapper().disableLogicDel().count().intValue() + 1);

        //为视频生成短id,方便搜素和记忆
        int tryCount = 0;
        do {
            if (tryCount++ > 10) {
                log.error("视频sid已用完");
                throw new CustomException("上传视频失败,请重试！");
            }
            video.setSid(RandomUtil.randomString(6).toLowerCase());
        } while (existsBy(Video::getSid, video.getSid()));

        //插入视频
        save(video);

        //创建节目
        if (dto.getType().isBangumi()) {
            dto.getBangumi().setVideoId(video.getId());
            Long bangumiId = bangumiService.createBangumi(dto.getBangumi());
            updateById(video.getId(), Video::getBangumiId, bangumiId);
        }

        //创建分集
        for (VideoPartDto partDto : dto.getCreateParts()) {
            partDto.setVideoId(video.getId());
            partDto.setAppend(0);
            videoPartService.createVideoPart(partDto);
        }
        //创建动态
        userDynamicService.createDynamic(BusinessType.VIDEO, video.getId(), dto.getDynamic());
        //设置禁用,审核完毕后再开放
        userDynamicService.updateDynamic(video.getId(), UserDynamic::getDisabled, 1);
        //发布事件,通知其它业务做出响应
        publishEvent(new VideoEvent(this, video, EventAction.INSERT));
        return video.getId();
    }
    /**
     * 分集增加/删除时重新统计能播放的时长
     */
    @EventListener(VideoPartEvent.class)
    public void listen(VideoPartEvent e) {
        Long videoId = e.getEntity().getVideoId();
        Video video = getById(videoId);
        if (video != null) {
            //增加/删除视频后重新统计时长
            int duration = videoPartService.sumDuration(videoId);
            updateById(videoId, Video::getDuration, duration);
            //如果能播放的分集数为0则该视频不能被搜索
            if (video.getSearch() == 1) {
                int count = videoPartService.countCanplay(videoId, true);
                if (count == 0) {
                    updateById(videoId, Video::getSearch, 0);
                }
            }
        }
    }
    /**
     * 审核通过时,启用动态
     */
    @EventListener(SysAuditEvent.class)
    public void listen(SysAuditEvent e) {
        if (e.getSysAudit().getAuditType() == AuditType.VIDEO_PART) {
            VideoPart part = videoPartService.getById(e.getSysAudit().getTargetId());
            if (part != null) {
                userDynamicService.updateDynamic(part.getVideoId(), UserDynamic::getDisabled, 0);
            }
        }
    }
    /**
     * 删除动态时,也会删除视频
     */
    @EventListener(UserDynamicEvent.class)
    public void listen(UserDynamicEvent e) {
        if (e.isDelete() && e.getEntity().getDynamicType() == BusinessType.VIDEO) {
            removeById(e.getEntity().getTargetId());
        }
    }
    /**
     * 定时检查可以被搜索到的视频,然后设置search=1
     * search=1的条件：
     *  1.至少一条part通过审核
     *  2.动态visible=1
     *  3.动态过了发布时间
     */
    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void check() {
        List<Long> list = wrapper()
                .distinct()
                .select(Video::getId)
                .innerJoin(UserDynamic.class, UserDynamic::getTargetId, Video::getId)
                .innerJoin(VideoPart.class, VideoPart::getVideoId, Video::getId)
                .eq(VideoPart::getCanplay, 1)
                .eq(UserDynamic::getVisible, 1)
                .eq(UserDynamic::getPublishFlag, 0)
                .eq(UserDynamic::getDisabled, 0)
                .eq(Video::getDisabled, 0)
                .eq(Video::getSearch, 0)
                .list()
                .stream()
                .map(Video::getId)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(list)) {
            lambdaUpdate().set(Video::getSearch, 1)
                    .in(Video::getId, list)
                    .update(new Video());
        }
    }

    @EventListener(UserReportAuditEvent.class)
    public void listen(UserReportAuditEvent e) {
        if (e.getReportType() == BusinessType.VIDEO) {
            Video video = getById(e.getTargetId());
            if (video == null) {
                return;
            }
            NoticeType noticeType;
            if (e.getAuditStatus() == AuditStatus.SUCCESS) {
                noticeType = NoticeType.VIDEO_REPORT_SUCCESS;
                updateById(video.getId(), Video::getDisabled, 1);
                updateById(video.getId(), Video::getReason, e.getMessage());
                //通知视频作者视频已下架
                userNoticeService.createNotice(NoticeType.VIDEO_VIOLATION, video.getUserId(), "", video.getTitle(), e.getMessage());
            } else {
                noticeType = NoticeType.VIDEO_REPORT_FAIL;
            }
            for (Long userId : e.getUserIds()) {
                //通知举报者成功或失败
                userNoticeService.createNotice(noticeType, userId, "", video.getTitle());
            }
        }
    }
}