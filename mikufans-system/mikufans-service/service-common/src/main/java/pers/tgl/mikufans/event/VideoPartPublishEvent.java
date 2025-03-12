package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pers.tgl.mikufans.domain.video.VideoPart;

/**
 * 预处理完成->审核完成->过了发布时间->触发事件
 */
@Getter
public class VideoPartPublishEvent extends ApplicationEvent {
    private final VideoPart videoPart;

    public VideoPartPublishEvent(Object source, VideoPart videoPart) {
        super(source);
        this.videoPart = videoPart;
    }
}