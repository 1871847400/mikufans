package pers.tgl.mikufans.domain.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.enums.AuditType;
import pers.tgl.mikufans.validator.group.Update;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@PermFlag(name = "审核管理", group = PermGroup.BUSINESS)
public class SysAudit extends SystemBaseEntity {
    /**
     * 目标id
     */
    @NotNull
    private Long targetId;
    /**
     * 审核类型
     */
    @NotNull
    private AuditType auditType;
    /**
     * 审核状态
     */
    @NotNull(message = "请选择审核状态", groups = Update.class)
    private AuditStatus auditStatus;
    /**
     * 原因
     */
    @NotNull
    private String auditReason;
}