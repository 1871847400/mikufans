package pers.tgl.mikufans.params;

import lombok.Data;

@Data
public class UserStarParams {
    /**
     * 目标用户id
     */
    private Long userId;
    /**
     * 关联视频id
     */
    private Long videoId;
}