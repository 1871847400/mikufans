package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoRepresent;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoRepresentVo extends VideoRepresent {
    /**
     * 关联视频
     */
    private Video video;
}