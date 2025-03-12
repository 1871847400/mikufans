package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.user.UserStar;
import pers.tgl.mikufans.domain.video.VideoStar;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserStarVo extends UserStar {
    /**
     * 收藏信息
     */
    private VideoStar videoStar;
    /**
     * 点赞数据
     */
    private LikeStatus likeStatus;
}