package pers.tgl.mikufans.controller.business;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.service.VideoAuditService;
import pers.tgl.mikufans.vo.VideoAudit;

@RestController
@RequestMapping("/admin/video/audit")
@RequiredArgsConstructor
public class VideoAuditController extends BaseController {
    private final VideoAuditService videoAuditService;

    @PreAuthorize("hasPermission('video_audit', 'select')")
    @GetMapping
    public IPage<VideoAudit> getPage() {
        return videoAuditService.search();
    }

    @PreAuthorize("hasPermission('video_audit', 'select')")
    @GetMapping("/{vid}")
    public VideoAudit getVideoAudit(@PathVariable Long vid) {
        return videoAuditService.getVideoAudit(vid);
    }
}