package pers.tgl.mikufans.event;

import lombok.Getter;
import pers.tgl.mikufans.domain.video.Video;

@Getter
public class VideoEvent extends BaseEntityEvent<Video> {
    public VideoEvent(Object source, Video entity, EventAction action) {
        super(source, entity, action);
    }
}