package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.video.VideoStar;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoStarVo extends VideoStar {
    /**
     * 关联的视频
     */
    private VideoVo video;
    /**
     * 高亮标题
     */
    private String highlighted;
}