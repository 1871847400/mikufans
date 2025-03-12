package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pers.tgl.mikufans.domain.bangumi.BangumiSubscribe;

@Getter
public class SubscribeEvent extends ApplicationEvent {
    private final BangumiSubscribe subscribe;
    private final boolean delete;

    public SubscribeEvent(Object source, BangumiSubscribe subscribe, boolean delete) {
        super(source);
        this.subscribe = subscribe;
        this.delete = delete;
    }
}
