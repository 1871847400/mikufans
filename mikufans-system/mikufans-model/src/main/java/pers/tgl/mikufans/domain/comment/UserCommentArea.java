package pers.tgl.mikufans.domain.comment;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.enums.CommentFlag;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@PermFlag(name = "评论区", group = PermGroup.BUSINESS)
public class UserCommentArea extends UserBaseEntity {
    /**
     * 评论设置
     */
    private CommentFlag commentFlag;
    /**
     * 评论最低用户等级
     */
    private Integer userLevel;
    /**
     * 评论数量
     */
    private Integer comments;
    /**
     * 最新评论id
     */
    private Long newCommentId;
    /**
     * 最热评论id
     */
    private Long hotCommentId;
    /**
     * 最早的评论id
     */
    private Long fstCommentId;
    /**
     * 当前置顶的评论id
     */
    private Long topCommentId;
    /**
     * 业务类型
     */
    private BusinessType busiType;
    /**
     * 业务id
     */
    private Long busiId;
}