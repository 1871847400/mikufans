package pers.tgl.mikufans.event;

import lombok.Getter;
import pers.tgl.mikufans.domain.user.UserDynamic;

@Getter
public class UserDynamicEvent extends BaseEntityEvent<UserDynamic> {
    public UserDynamicEvent(Object source, UserDynamic entity, EventAction action) {
        super(source, entity, action);
    }
}
