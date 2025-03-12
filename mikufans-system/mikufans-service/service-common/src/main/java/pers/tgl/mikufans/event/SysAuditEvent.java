package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pers.tgl.mikufans.domain.system.SysAudit;

@Getter
public class SysAuditEvent extends ApplicationEvent {
    private final SysAudit sysAudit;

    public SysAuditEvent(Object source, SysAudit sysAudit) {
        super(source);
        this.sysAudit = sysAudit;
    }
}