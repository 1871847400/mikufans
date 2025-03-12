package pers.tgl.mikufans.vo;

import lombok.Data;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.enums.BusinessType;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ReportAuditDto {
    @NotNull
    private Long targetId;

    @NotNull
    private BusinessType reportType;

    @NotBlank(message = "请输入审核结论")
    private String message;

    @NotNull(message = "请选择审核状态")
    private AuditStatus audit;

    @AssertFalse(message = "请选择审核状态")
    public boolean isError() {
        return audit == AuditStatus.UNKNOWN;
    }
}