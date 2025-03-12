package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.enums.BusinessType;

import java.util.List;

/**
 */
@Getter
public class UserReportAuditEvent extends ApplicationEvent {
    private final Long targetId;
    private final BusinessType reportType;
    private final String message;
    private final AuditStatus auditStatus;
    /**
     * 参与举报的用户ID列表
     */
    private final List<Long> userIds;

    public UserReportAuditEvent(Object source, Long targetId, BusinessType reportType, String message, AuditStatus auditStatus, List<Long> userIds) {
        super(source);
        this.targetId = targetId;
        this.reportType = reportType;
        this.message = message;
        this.auditStatus = auditStatus;
        this.userIds = userIds;
    }
}