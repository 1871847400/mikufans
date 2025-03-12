package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.system.SysAudit;

public interface SysAuditService extends BaseService<SysAudit> {
    /**
     * 获取审核状态
     */
    AuditStatus getAuditStatus(Long targetId);
    /**
     * 修改审核状态
     */
    void setAuditStatus(Long targetId, AuditStatus status);
}