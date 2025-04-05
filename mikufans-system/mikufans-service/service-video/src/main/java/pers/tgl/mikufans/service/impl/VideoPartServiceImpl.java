package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.consts.*;
import pers.tgl.mikufans.domain.common.UploadTask;
import pers.tgl.mikufans.domain.enums.*;
import pers.tgl.mikufans.domain.system.SysAudit;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.domain.video.VideoProcess;
import pers.tgl.mikufans.domain.video.VideoResource;
import pers.tgl.mikufans.dto.SubtitleFile;
import pers.tgl.mikufans.dto.VideoPartDto;
import pers.tgl.mikufans.event.*;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.VideoPartMapper;
import pers.tgl.mikufans.model.MediaInfo;
import pers.tgl.mikufans.service.*;
import pers.tgl.mikufans.util.*;
import pers.tgl.mikufans.vo.VideoPartVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.Duration;
import java.util.*;

/**
 * @author TGL
 * @description 针对表【video_part(视频分集表)】的数据库操作Service实现
 * @createDate 2022-12-31 10:17:31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VideoPartServiceImpl extends BaseServiceImpl<VideoPart, VideoPartMapper> implements VideoPartService {
    @Lazy
    @Resource
    private VideoService videoService;

    private final VideoResourceService videoResourceService;
    private final VideoStatsService videoStatsService;
    private final RedisUtils redisUtils;
    private final UploadTaskService uploadTaskService;
    private final VideoFlagService videoFlagService;
    private final VideoProcessService videoProcessService;
    private final VideoSubtitleService videoSubtitleService;
    private final AppConfig appConfig;
    private final SysAuditService sysAuditService;
    private final TransactionTemplate transactionTemplate;
    private final UserNoticeService userNoticeService;

    @Override
    public List<VideoPartVo> getVideoParts(Long videoId) {
        List<VideoPartVo> list = wrapper()
                .selectAll(VideoPart.class)
                .eq(VideoPart::getVideoId, videoId)
                .eq(VideoPart::getDisabled, 0)
                .orderByAsc(VideoPart::getSort, VideoPart::getCreateTime)
                .list(VideoPartVo.class);
        for (VideoPartVo vo : list) {
            vo.setResource(videoResourceService.getById(vo.getResId()));
            vo.setSubtitles(videoSubtitleService.listByResId(vo.getResId()));
            vo.setSysAudit(sysAuditService.getOneBy(SysAudit::getTargetId, vo.getId()));
            VideoProcess process = videoProcessService.getOneBy(VideoProcess::getPartId, vo.getId());
            if (process != null) {
                vo.setProgress(videoProcessService.getProgress(process.getId()));
                vo.setProcessFail(Objects.equals(process.getResult(), 2));
            } else {
                vo.setProgress(0);
                vo.setProcessFail(false);
            }
        }
        return list;
    }

    @Override
    public VideoPartVo getFirst(Long videoId) {
        return wrapper().eq(VideoPart::getVideoId, videoId)
                .eq(VideoPart::getDisabled, 0)
                .eq(VideoPart::getCanplay, 1)
                .last("limit 1")
                .one(VideoPartVo.class);
    }

    @Override
    public int countCanplay(Long videoId, boolean canplay) {
        return wrapper()
                .eq(VideoPart::getVideoId, videoId)
                .eq(VideoPart::getDisabled, 0)
                .eq(VideoPart::getCanplay, canplay ? 1 : 0)
                .count()
                .intValue();
    }

    @Override
    public Map<AuditStatus, Integer> countAuditStatus(Long videoId) {
        Map<AuditStatus, Integer> result = new LinkedHashMap<>(3);
        for (AuditStatus status : AuditStatus.values()) {
            Long count = wrapper()
                    .innerJoin(SysAudit.class, SysAudit::getTargetId, VideoPart::getId)
                    .eq(SysAudit::getAuditStatus, status)
                    .eq(VideoPart::getVideoId, videoId)
                    .eq(VideoPart::getDisabled, 0)
                    .eq(VideoPart::getCanplay, 0)
                    .count();
            result.put(status, count.intValue());
        }
        return result;
    }

    @Override
    public int sumDuration(Long videoId) {
        Video video = wrapper()
                .selectSum(VideoResource::getDuration, Video::getDuration)
                .innerJoin(VideoResource.class, VideoResource::getId, VideoPart::getResId)
                .eq(VideoPart::getVideoId, videoId)
                .eq(VideoPart::getDisabled, 0)
                .eq(VideoPart::getCanplay, 1)
                .one(Video.class);
        return video != null ? video.getDuration() : 0;
    }

    @Override
    public VideoPartVo getVoById(Long id) {
        VideoPartVo vo = getById(id, VideoPartVo.class);
        if (vo != null) {
            vo.setResource(videoResourceService.getById(vo.getResId()));
            vo.setSubtitles(videoSubtitleService.listByResId(vo.getResId()));
        }
        return vo;
    }

    @Override
    public int getWatchLevel(Long partId) {
        Long videoId = getColumnValue(partId, VideoPart::getVideoId);
        Video one = Db.lambdaQuery(Video.class)
                .select(Video::getUserLevel)
                .eq(Video::getId, videoId)
                .one();
        return one != null ? one.getUserLevel() : 0;
    }

    @Override
    @Cacheable(value = "video-resource#30m", key = "#partId")
    public VideoResource findVideoResource(Long partId) {
        return new MPJLambdaWrapper<>(VideoResource.class)
                .selectAll(VideoResource.class)
                .innerJoin(VideoPart.class, VideoPart::getResId, VideoResource::getId)
                .eq(VideoPart::getId, partId)
                .one();
    }

    @Override
    public Long createVideoPart(VideoPartDto dto) {
        //检查是否有主视频的权限
        Video video = videoService.getById(dto.getVideoId());
        checkUserPermission(video);
        //限制最大数量
        long count = countBy(VideoPart::getVideoId, dto.getVideoId());
        if (count >= Consts.VIDEO_PART_MAX_COUNT) {
            throw new CustomException("超过集数上限" + Consts.VIDEO_PART_MAX_COUNT + "了");
        }
        VideoPart videoPart = BeanUtil.toBean(dto, VideoPart.class);
        String sid = videoService.getColumnValue(dto.getVideoId(), Video::getSid);
        if (StrUtil.isBlank(sid)) {
            throw new CustomException("创建失败");
        }
        videoPart.setVideoSid(sid);
        if (videoPart.getSort() == null) {
            Integer maxSort = getMax(VideoPart::getSort, VideoPart::getVideoId, dto.getVideoId()).orElse(0);
            videoPart.setSort(maxSort + 1);
        }
        videoPart.setPartType(VideoPartType.TV);
        UploadTask uploadTask = uploadTaskService.getById(dto.getTaskId());
        if (uploadTask == null || FileUtil.isEmpty(uploadTask.getOutput())) {
            throw new CustomException("上传文件已失效,请重新上传！");
        }
        VideoResource videoResource = new VideoResource();
        //手动生成雪花id
        long resId = IdWorker.getId();
        videoResource.setId(resId);
        videoResource.setMd5(uploadTask.getMd5());
        videoResource.setFileSize(uploadTask.getFileSize());
        videoResource.setDuration(0);
        videoResource.setTransferMode(TransferMode.NONE);
        videoResource.setTransferPath("");
        try {
            videoResource.setMediaInfo(FFmpegUtils.parse(uploadTask.getOutput().getAbsolutePath()));
        } catch (Exception e) {
            log.error("获取视频元数据失败", e);
            videoResource.setMediaInfo(new MediaInfo());
        }
        //设置实际的画质列表,不会跟随配置文件变化而变化
//        videoResource.setQualities(Arrays.asList(VideoQuality.HD.name(), VideoQuality.SD.name()));
        //设置状态为处理中
        videoResource.setPending(1);
        //设置媒体格式(上传时获取到的)
        videoResource.setMediaType(uploadTask.getMediaType());
        String basePath = appConfig.getUpload().get(FileMediaType.VIDEO).getPath();
        //当前日期例如：24-12-31
        String date = DateUtil.formatDate(new Date()).substring(2);
        //本地文件保存位置
        String localPath = basePath + "/" + date + "/" + dto.getVideoId() + "/" + resId;
        videoResource.setLocalPath(localPath);
        videoResource.setQualityLevel(0);
        //保存到数据库,生成id
        Db.save(videoResource);
        //创建物理目录
        FileUtil.mkdir(localPath);
        //将上传的文件从临时目录复制到资源目录
        //但不要删除上传的文件,可能会被复用
        String filename = "video." + FileUtil.getSuffix(uploadTask.getOutput());
        File originFile = new File(localPath, filename);
        FileUtil.copy(uploadTask.getOutput(), originFile, true);
        videoPart.setResId(resId);
        globalSet(dto, dto.getVideoId(), videoResource.getId());
        return transactionTemplate.execute(status->{
            if (save(videoPart)) {
//                calcSort(videoPart.getVideoId());
                publishEvent(new VideoPartEvent(this, videoPart, EventAction.INSERT));
            }
            //创建视频文件处理器
            VideoProcess process = new VideoProcess();
            process.setParams(dto.getPreprocess());
            process.setStage(VideoProcessStage.SEGMENT.getCode());
            process.setVideoId(dto.getVideoId());
            process.setPartId(videoPart.getId());
            process.setResId(videoResource.getId());
            process.setFilepath(originFile.getAbsolutePath());
            process.setProgress(0);
            Db.save(process);
            return videoPart.getId();
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVideoPart(VideoPartDto dto) {
        VideoPart part = getById(dto.getId());
        checkUserPermission(part);
        if (updateById(BeanUtil.toBean(dto, VideoPart.class))) {
//            calcSort(part.getVideoId());
            if (isValidId(part.getResId())) {
                globalSet(dto, part.getVideoId(), part.getResId());
            }
            publishEvent(new VideoPartEvent(this, getById(dto.getId()), EventAction.UPDATE));
        }
    }

    /**
     * 创建和删除的公共操作
     */
    private void globalSet(VideoPartDto dto, Long videoId, Long resId) {
        if (CollUtil.isNotEmpty(dto.getSubtitles())) {
            for (SubtitleFile subtitleFile : dto.getSubtitles()) {
                videoSubtitleService.createSubtitle(videoId, resId, subtitleFile);
            }
        }
    }

    @Override
    public void recordPlayCount(Long partId, HttpServletRequest request) {
        Long videoId = getColumnValue(partId, VideoPart::getVideoId);
        final String redisKeyPrefix = "video-play-record:" + videoId + ":";
        Long contextUserId = SecurityUtils.getContextUserId(false);
        String remoteAddr = IpUtils.getIpaddr(request);
        if (contextUserId != null && redisUtils.exists(redisKeyPrefix + contextUserId)
                || redisUtils.exists(redisKeyPrefix + remoteAddr)) {
            return;
        }
        redisUtils.set(redisKeyPrefix + remoteAddr, "", Duration.ofDays(1));
        if (contextUserId != null) {
            redisUtils.set(redisKeyPrefix + contextUserId, "", Duration.ofDays(1));
        }
        videoStatsService.addPlay(videoId, 1);
    }

    @Override
    @Deprecated
    public Map<VideoStatus, PageImpl<VideoPartVo>> getUploadParts(Long videoId, @Nullable VideoStatus videoStatus) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        Map<VideoStatus, PageImpl<VideoPartVo>> result = new LinkedHashMap<>();
//        for (UploadStatus status : UploadStatus.values()) {
//            PageImpl<VideoPartVo> page = BaseController.createPage();
//            MPJLambdaWrapper<VideoPart> wrapper = wrapper().leftJoin(VideoResource.class, VideoResource::getId, VideoPart::getResId)
//                    .selectAssociation(VideoResource.class, VideoPartVo::getResource)
//                    .eq(VideoPart::getVideoId, videoId)
//                    .eq(VideoPart::getUserId, contextUserId)
//                    .eq(status == UploadStatus.DOING, VideoResource::getPending, 1)
//                    .eq(status == UploadStatus.SUCCESS, VideoResource::getPending, 0)
//                    .eq(status == UploadStatus.FAIL, VideoResource::getPending, 2)
//                    .orderByAsc(VideoPart::getSort, VideoPart::getCreateTime);
//            if (status != uploadStatus) {
//                page.setTotal(wrapper.count());
//            } else {
//                wrapper.page(page, VideoPartVo.class);
//                page.fill(vo -> {
//                    vo.setSubtitles(videoSubtitleService.listBy(VideoSubtitle::getRid, vo.getResId()));
//                    VideoProcess process = videoProcessService.getOneBy(VideoProcess::getPartId, vo.getId());
//                    if (process != null) {
//                        vo.setProgress(process.getProgress());
//                    }
//                });
//            }
//            result.put(status, page);
//        }
        return result;
    }

    /**
     * 重新计算排序
     */
    private void calcSort(Long videoId) {
        List<VideoPart> list = wrapper()
                .select(Video::getId)
                .eq(VideoPart::getVideoId, videoId)
                .orderByAsc(VideoPart::getSort, VideoPart::getCreateTime)
                .list();
        if (!list.isEmpty()) {
            list.forEach(p -> {
                p.setSort(list.indexOf(p) + 1);
            });
            super.updateBatchById(list);
        }
    }

    @Override
    public boolean removeById(VideoPart entity) {
        if (super.removeById(entity)) {
            //删除数据后需要重新排序
            calcSort(entity.getVideoId());
            publishEvent(new VideoPartEvent(this, entity, EventAction.DELETE));
            return true;
        }
        return false;
    }

    @EventListener(VideoEvent.class)
    public void listen(VideoEvent e) {
        if (e.getAction() == EventAction.DELETE) {
            List<VideoPart> videoParts = listBy(VideoPart::getVideoId, e.getEntity().getId());
            videoParts.forEach(this::removeById);
        }
    }

    @EventListener(VideoDanmuEvent.class)
    public void listen(VideoDanmuEvent e) {
        Long partId = e.getEntity().getPartId();
        Long videoId = getColumnValue(partId, VideoPart::getVideoId);
        int delta = 0;
        if (e.getAction() == EventAction.INSERT) {
            delta = 1;
        } else if (e.getAction() == EventAction.DELETE) {
            delta = -1;
        }
        videoStatsService.addDanmu(videoId, delta);
        incrementById(partId, VideoPart::getDanmus, delta);
    }

    @EventListener(SysAuditEvent.class)
    public void listen(SysAuditEvent e) {
        SysAudit sysAudit = e.getSysAudit();
        if (sysAudit.getAuditType() == AuditType.VIDEO_PART) {
            AuditStatus status = sysAudit.getAuditStatus();
            VideoPart videoPart = getById(sysAudit.getTargetId());
            if (status == AuditStatus.UNKNOWN || videoPart == null) {
                return;
            }
            Video video = videoService.getById(videoPart.getVideoId());
            if (video == null) {
                return;
            }
            if (status == AuditStatus.SUCCESS && updateById(videoPart.getId(), VideoPart::getCanplay, 1)) {
                publishEvent(new VideoPartEvent(this, videoPart, EventAction.UPDATE));
            }
            NoticeType noticeType = NoticeType.VIDEO_AUDIT_FAIL;
            if (status == AuditStatus.SUCCESS) {
                noticeType = NoticeType.VIDEO_AUDIT_SUCCESS;
            }
            String param1 = video.getTitle() + " - p" + videoPart.getSort() + ":" + videoPart.getPartName();
            String param2 = sysAudit.getAuditReason();
            userNoticeService.createNotice(noticeType, videoPart.getUserId(), videoPart.getUri(), param1, param2);
        }
    }


    @EventListener(VideoPartProcessEvent.class)
    public void listen(VideoPartProcessEvent e) {
        SysAudit sysAudit = new SysAudit();
        sysAudit.setAuditType(AuditType.VIDEO_PART);
        sysAudit.setTargetId(e.getVideoProcess().getPartId());
        sysAudit.setAuditStatus(AuditStatus.UNKNOWN);
        sysAudit.setAuditReason("");
        sysAudit.setCreateBy(0L);
        Db.save(sysAudit);
    }
}