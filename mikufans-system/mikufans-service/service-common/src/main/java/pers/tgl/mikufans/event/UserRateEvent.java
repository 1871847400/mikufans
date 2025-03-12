package pers.tgl.mikufans.event;

import lombok.Getter;
import pers.tgl.mikufans.domain.user.UserRate;

@Getter
public class UserRateEvent extends BaseEntityEvent<UserRate> {
    public UserRateEvent(Object source, UserRate entity, EventAction action) {
        super(source, entity, action);
    }
}
