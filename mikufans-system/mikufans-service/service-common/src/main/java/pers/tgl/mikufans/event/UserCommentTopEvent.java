package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserCommentTopEvent extends ApplicationEvent {
    private final Long areaId;
    private final Long commentId;
    private final int top;

    public UserCommentTopEvent(Object source, Long areaId, Long commentId, int top) {
        super(source);
        this.areaId = areaId;
        this.commentId = commentId;
        this.top = top;
    }
}