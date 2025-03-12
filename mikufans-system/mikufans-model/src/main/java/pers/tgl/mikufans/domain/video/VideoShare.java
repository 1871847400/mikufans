package pers.tgl.mikufans.domain.video;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoShare extends UserBaseEntity {
    /**
     * 分享码
     */
    private String shareCode;
    /**
     * 分享的视频id
     */
    private Long videoId;
    /**
     * 分享成功次数
     */
    private Integer shareCount;
}