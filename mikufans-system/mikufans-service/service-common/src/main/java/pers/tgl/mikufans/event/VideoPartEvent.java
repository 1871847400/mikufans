package pers.tgl.mikufans.event;

import pers.tgl.mikufans.domain.video.VideoPart;

public class VideoPartEvent extends BaseEntityEvent<VideoPart> {
    public VideoPartEvent(Object source, VideoPart entity, EventAction action) {
        super(source, entity, action);
    }
}