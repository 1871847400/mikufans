package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pers.tgl.mikufans.domain.video.VideoCoin;

@Getter
public class PayCoinEvent extends ApplicationEvent {
    private final VideoCoin videoCoin;

    public PayCoinEvent(Object source, VideoCoin videoCoin) {
        super(source);
        this.videoCoin = videoCoin;
    }
}
