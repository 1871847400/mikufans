package pers.tgl.mikufans.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.system.SysAudit;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.SysAuditMapper;
import pers.tgl.mikufans.service.SysAuditService;

@Service
@RequiredArgsConstructor
public class SysAuditServiceImpl extends BaseServiceImpl<SysAudit, SysAuditMapper> implements SysAuditService {
    @Override
    public AuditStatus getAuditStatus(Long targetId) {
        SysAudit sysAudit = getOneBy(SysAudit::getTargetId, targetId);
        if (sysAudit == null) {
            return AuditStatus.UNKNOWN;
        }
        return sysAudit.getAuditStatus();
    }

    @Override
    public void setAuditStatus(Long targetId, AuditStatus status) {
        SysAudit sysAudit = getOneBy(SysAudit::getTargetId, targetId);
        if (sysAudit == null) {
            throw new CustomException("审核信息不存在");
        }
        sysAudit.setAuditStatus(status);
        updateById(sysAudit);
    }
}