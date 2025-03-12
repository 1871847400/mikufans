package pers.tgl.mikufans.controller.system;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.system.SysAudit;
import pers.tgl.mikufans.event.SysAuditEvent;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.service.SysAuditService;
import pers.tgl.mikufans.vo.SysAuditDto;

@RestController
@RequestMapping("/admin/system/audit")
@RequiredArgsConstructor
public class SysAuditController extends BaseController {
    private final SysAuditService sysAuditService;
    private final ApplicationEventPublisher eventPublisher;

    @AppLog("提交审核")
    @PreAuthorize("hasPermission('sys_audit', 'update')")
    @PutMapping
    public SysAudit audit(@RequestBody @Validated SysAuditDto dto) {
        SysAudit sysAudit = sysAuditService.getById(dto.getId());
        if (sysAudit == null || sysAudit.getAuditStatus() != AuditStatus.UNKNOWN) {
            throw new CustomException("审核信息不存在或已经审核过了");
        }
        if (Db.updateById(BeanUtil.toBean(dto, SysAudit.class))) {
            SysAudit result = sysAuditService.getById(dto.getId());
            if (result != null) {
                eventPublisher.publishEvent(new SysAuditEvent(this, result));
                return result;
            }
        }
        throw new CustomException("审核失败");
    }
}