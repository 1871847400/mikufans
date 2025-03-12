package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SearchEvent extends ApplicationEvent {
    private final SearchType searchType;
    private final String keyword;

    public SearchEvent(Object source, SearchType searchType, String keyword) {
        super(source);
        this.searchType = searchType;
        this.keyword = keyword;
    }

    public enum SearchType {
        VIDEO,
        USER
    }
}