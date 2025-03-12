package pers.tgl.mikufans.vo;

import lombok.Data;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.model.MessageModel;

@Data
public class UserReportVo {
    /**
     * 业务类型
     */
    private BusinessType reportType;
    /**
     * 业务id
     */
    private Long targetId;
    /**
     * 业务对象
     */
    private MessageModel target;
    /**
     * 被举报次数
     */
    private Integer reportCount;
}