package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.comment.UserCommentArea;
import pers.tgl.mikufans.domain.user.UserDynamic;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDynamicVo extends UserDynamic {
    /**
     * 点赞数据
     */
    private LikeStatus likeStatus;
    /**
     * 评论区
     */
    private UserCommentArea commentArea;
    /**
     * 业务对象
     */
    private UserBaseEntity source;
}