package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pers.tgl.mikufans.model.UserToken;

/**
 * 用户刷新token时,包括登录时也会触发
 */
@Getter
public class UserTokenEvent extends ApplicationEvent {
    private final UserToken userToken;

    public UserTokenEvent(Object source, UserToken userToken) {
        super(source);
        this.userToken = userToken;
    }
}
