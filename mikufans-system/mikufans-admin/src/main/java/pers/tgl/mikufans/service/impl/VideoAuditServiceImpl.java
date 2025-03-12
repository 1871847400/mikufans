package pers.tgl.mikufans.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.enums.AuditType;
import pers.tgl.mikufans.domain.system.SysAudit;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.mapper.SysAuditMapper;
import pers.tgl.mikufans.service.VideoAuditService;
import pers.tgl.mikufans.service.VideoPartService;
import pers.tgl.mikufans.service.VideoService;
import pers.tgl.mikufans.vo.VideoAudit;
import pers.tgl.mikufans.vo.VideoPartVo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoAuditServiceImpl extends BaseServiceImpl<SysAudit, SysAuditMapper> implements VideoAuditService {
    private final VideoService videoService;
    private final VideoPartService videoPartService;

    @Override
    public IPage<VideoAudit> search() {
        return wrapper()
                .selectAll(Video.class)
                .selectCount(SysAudit::getId, VideoAudit::getAuditCount)
                .innerJoin(VideoPart.class, VideoPart::getId, SysAudit::getTargetId)
                .innerJoin(Video.class, Video::getId, VideoPart::getVideoId)
                .eq(SysAudit::getAuditType, AuditType.VIDEO_PART)
                .eq(SysAudit::getAuditStatus, AuditStatus.UNKNOWN)
                .groupBy(VideoPart::getVideoId)
                .page(BaseController.createPage(), VideoAudit.class);
    }

    @Override
    public VideoAudit getVideoAudit(Long videoId) {
        VideoAudit videoAudit = videoService.getById(videoId, VideoAudit.class);
        if (videoAudit == null) {
            return null;
        }
        List<VideoPartVo> parts = videoPartService.wrapper()
                .selectAssociation(SysAudit.class, VideoPartVo::getSysAudit)
                .innerJoin(SysAudit.class, SysAudit::getTargetId, VideoPart::getId)
                .eq(VideoPart::getVideoId, videoId)
                .eq(SysAudit::getAuditStatus, AuditStatus.UNKNOWN)
                .list(VideoPartVo.class);
        videoAudit.setParts(parts);
        videoAudit.setAuditCount(parts.size());
        return videoAudit;
    }
}