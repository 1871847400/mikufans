package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pers.tgl.mikufans.domain.article.Article;

@Getter
public class UserArticleEvent extends ApplicationEvent {
    private final Article article;
    private final EventAction eventAction;

    public UserArticleEvent(Object source, Article article, EventAction eventAction) {
        super(source);
        this.article = article;
        this.eventAction = eventAction;
    }
}
