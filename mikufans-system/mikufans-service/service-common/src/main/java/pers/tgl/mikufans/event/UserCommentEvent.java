package pers.tgl.mikufans.event;

import lombok.Getter;
import pers.tgl.mikufans.domain.comment.UserComment;

@Getter
public class UserCommentEvent extends BaseEntityEvent<UserComment> {
    public UserCommentEvent(Object source, UserComment entity, EventAction action) {
        super(source, entity, action);
    }
}