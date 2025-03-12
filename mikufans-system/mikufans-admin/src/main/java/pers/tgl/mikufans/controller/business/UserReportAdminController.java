package pers.tgl.mikufans.controller.business;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.user.UserReport;
import pers.tgl.mikufans.service.ReportAdminService;
import pers.tgl.mikufans.vo.ReportAuditDto;
import pers.tgl.mikufans.vo.UserReportVo;

@RestController
@RequestMapping("/admin/user/report")
@RequiredArgsConstructor
public class UserReportAdminController extends BaseController {
    private final ReportAdminService reportAdminService;

    @PreAuthorize("hasPermission('user_report', 'select')")
    @GetMapping
    public IPage<UserReportVo> search(UserReport params) {
        return reportAdminService.search(params);
    }

    @PreAuthorize("hasPermission('user_report', 'select')")
    @GetMapping("/{targetId}")
    public IPage<UserReport> list(@PathVariable Long targetId) {
        return reportAdminService.getReportList(targetId);
    }

    @AppLog("审核举报")
    @PreAuthorize("hasPermission('user_report', 'update')")
    @PutMapping("/audit")
    public void audit(@RequestBody @Validated ReportAuditDto dto) {
        reportAdminService.audit(dto);
    }
}