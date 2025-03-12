package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pers.tgl.mikufans.domain.user.UserFavor;

@Getter
@Deprecated
public class UserFavorEvent extends ApplicationEvent {
    private final UserFavor userFavor;
    private final int newValue;
    private final int oldValue;
    /**
     * 第一次评价
     */
    private final boolean first;

    public UserFavorEvent(Object source, UserFavor userFavor, int newValue, int oldValue, boolean first) {
        super(source);
        this.userFavor = userFavor;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.first = first;
    }
}
