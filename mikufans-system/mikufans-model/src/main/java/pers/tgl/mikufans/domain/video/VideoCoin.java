package pers.tgl.mikufans.domain.video;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

/**
 * 视频投币表
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoCoin extends UserBaseEntity {
    /**
     * 视频id
     */
    private Long videoId;
    /**
     * 投币数量
     */
    private Integer coinCount;
}