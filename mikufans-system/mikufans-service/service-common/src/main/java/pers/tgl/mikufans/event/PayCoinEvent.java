package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pers.tgl.mikufans.domain.video.VideoCoin;

import java.math.BigDecimal;

@Getter
public class PayCoinEvent extends ApplicationEvent {
    private final VideoCoin videoCoin;
    /**
     * UP主实际收到的硬币数量
     */
    private final BigDecimal actualCount;

    public PayCoinEvent(Object source, VideoCoin videoCoin, BigDecimal actualCount) {
        super(source);
        this.videoCoin = videoCoin;
        this.actualCount = actualCount;
    }
}
