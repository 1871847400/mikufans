package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pers.tgl.mikufans.domain.base.BaseEntity;

/**
 * 通用实体CRUD事件
 */
@Getter
public class BaseEntityEvent<T extends BaseEntity> extends ApplicationEvent {
    private final T entity;
    private final EventAction action;

    public BaseEntityEvent(Object source, T entity, EventAction action) {
        super(source);
        this.entity = entity;
        this.action = action;
    }

    public boolean isInsert() {
        return action == EventAction.INSERT;
    }
    public boolean isUpdate() {
        return action == EventAction.UPDATE;
    }
    public boolean isDelete() {
        return action == EventAction.DELETE;
    }
}