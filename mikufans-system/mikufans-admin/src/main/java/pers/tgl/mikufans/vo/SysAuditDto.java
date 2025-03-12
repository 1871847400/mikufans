package pers.tgl.mikufans.vo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import pers.tgl.mikufans.domain.enums.AuditStatus;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;

@Data
public class SysAuditDto {
    @NotNull
    private Long id;
    /**
     * 审核状态
     */
    @NotNull(message = "请选择审核状态")
    private AuditStatus auditStatus;
    /**
     * 原因
     */
    @NotNull
    private String auditReason;

    @JsonIgnore
    @AssertFalse(message = "请选择审核状态")
    public boolean isError() {
        return auditStatus == AuditStatus.UNKNOWN;
    }

    @JsonIgnore
    @AssertFalse(message = "请填写不通过的原因")
    public boolean isError2() {
        return auditStatus==AuditStatus.FAIL && StrUtil.isBlank(auditReason);
    }
}