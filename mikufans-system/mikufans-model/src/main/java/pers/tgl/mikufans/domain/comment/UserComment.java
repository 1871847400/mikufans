package pers.tgl.mikufans.domain.comment;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.jackson.sensitive.Sensitive;
import pers.tgl.mikufans.jackson.sensitive.SensitiveType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户评论表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@PermFlag(name = "用户评论", group = PermGroup.BUSINESS)
@TableName(autoResultMap = true)
public class UserComment extends UserBaseEntity {
    /**
     * 评论区id
     */
    private Long areaId;
    /**
     * 楼层数(包含已删除的)
     * 结合areaId和pid计算
     */
    private Integer uid;
    /**
     * 父评论id 如果为0代表这是父评论
     * 和replyId不同，表示评论树根
     */
    private Long pid;
    /**
     * 回复的评论id 如果为0代表这是父评论
     */
    private Long replyId;
    /**
     * replyId != 0：目标评论的用户id
     * replyId == 0：评论区拥有者id
     */
    private Long replyUserId;
    /**
     * AT的用户列表
     * 评论内容里AT的用户昵称 ：用户id
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Long> atUsers;
    /**
     * 图片id列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> imageIds;
    /**
     * 子评论数量
     */
    private Integer childCount;
    /**
     * 评论内容
     */
    @Sensitive(SensitiveType.TERM)
    private String content;
    /**
     * 是否置顶 0否1是
     */
    private Integer top;
    /**
     * 热度分数,可能为负数
     */
    @Sensitive
    private BigDecimal score;
    /**
     * 是否为精选评论 0:否 1:是
     */
    private Integer selected;
    /**
     * 是否为子评论
     */
    @JsonProperty(value = "isChild")
    public boolean isChild() {
        return getPid() != null && getPid() != 0;
    }
    /**
     * 实际发布时间
     */
    public Date getPublishTime() {
        return getCreateTime();
    }
    /**
     * 回复的目标用户
     */
    public User getReplyUser() {
        return getUserBase(getReplyUserId());
    }
}