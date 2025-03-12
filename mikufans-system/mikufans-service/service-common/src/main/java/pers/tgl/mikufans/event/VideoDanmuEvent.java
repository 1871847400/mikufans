package pers.tgl.mikufans.event;

import lombok.Getter;
import pers.tgl.mikufans.domain.video.VideoDanmu;

@Getter
public class VideoDanmuEvent extends BaseEntityEvent<VideoDanmu> {
    public VideoDanmuEvent(Object source, VideoDanmu entity, EventAction action) {
        super(source, entity, action);
    }
}
