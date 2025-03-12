package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.video.VideoDanmu;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoDanmuVo extends VideoDanmu {
    private LikeStatus likeStatus;
}
