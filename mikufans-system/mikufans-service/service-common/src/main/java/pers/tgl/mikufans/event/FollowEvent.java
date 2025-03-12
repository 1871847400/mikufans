package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pers.tgl.mikufans.domain.user.UserFollow;

@Getter
public class FollowEvent extends ApplicationEvent {
    private final UserFollow userFollow;
    private final boolean delete;

    public FollowEvent(Object source, UserFollow userFollow, boolean delete) {
        super(source);
        this.userFollow = userFollow;
        this.delete = delete;
    }
}
