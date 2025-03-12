package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.user.UserLike;
import pers.tgl.mikufans.domain.user.UserLikeData;

/**
 * 用户点赞变化事件
 */
@Getter
public class UserLikeEvent extends ApplicationEvent {
    private final UserLikeData likeData;
    private final UserLike newLike;
    private final UserLike oldLike;

    public UserLikeEvent(Object source, UserLikeData likeData, UserLike newLike, @Nullable UserLike oldLike) {
        super(source);
        this.likeData = likeData;
        this.newLike = newLike;
        this.oldLike = oldLike;
    }
}