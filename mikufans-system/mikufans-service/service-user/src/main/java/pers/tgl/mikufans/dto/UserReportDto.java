package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.system.ReportBehavior;
import pers.tgl.mikufans.validator.db.DBExists;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserReportDto implements Serializable {
    /**
     * 举报内容id
     */
    @NotNull
    private Long targetId;
    /**
     * 举报类型
     */
    @NotNull
    private BusinessType reportType;
    /**
     * 行为id
     */
    @NotNull
    @DBExists(ReportBehavior.class)
    private Long behaviorId;
    /**
     * 详细理由
     */
    @Length(max = 500, message = "字数太多了")
    private String reason;
}