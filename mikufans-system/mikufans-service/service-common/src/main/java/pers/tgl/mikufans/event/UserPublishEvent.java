package pers.tgl.mikufans.event;

import lombok.Getter;
import pers.tgl.mikufans.domain.user.UserPublish;

@Getter
public class UserPublishEvent extends BaseEntityEvent<UserPublish> {
    public UserPublishEvent(Object source, UserPublish entity, EventAction action) {
        super(source, entity, action);
    }
}
