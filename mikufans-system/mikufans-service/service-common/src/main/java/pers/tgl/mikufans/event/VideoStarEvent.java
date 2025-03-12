package pers.tgl.mikufans.event;

import lombok.Getter;
import pers.tgl.mikufans.domain.video.VideoStar;

/**
 * 收藏视频事件
 */
@Getter
public class VideoStarEvent extends BaseEntityEvent<VideoStar> {
    /**
     * 用户在收藏或取消收藏后对该视频的收藏次数
     */
    private final int starCount;

    public VideoStarEvent(Object source, VideoStar entity, EventAction action, int starCount) {
        super(source, entity, action);
        this.starCount = starCount;
    }
}