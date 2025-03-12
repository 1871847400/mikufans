package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.comment.UserComment;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserCommentVo extends UserComment {
    /**
     * 热评
     */
    private List<UserCommentVo> hots;
    /**
     * 点赞数据
     */
    private LikeStatus likeStatus;
}