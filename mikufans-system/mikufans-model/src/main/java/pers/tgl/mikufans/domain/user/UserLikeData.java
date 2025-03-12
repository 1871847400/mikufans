package pers.tgl.mikufans.domain.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.enums.BusinessType;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@PermFlag(name = "用户点赞数据", group = PermGroup.BUSINESS)
@TableName(autoResultMap = true)
public class UserLikeData extends UserBaseEntity {
    /**
     * 点赞的业务类型
     */
    private BusinessType likeType;
    /**
     * 业务id
     */
    private Long busiId;
    /**
     * 点赞数量
     */
    private Integer likes;
    /**
     * 点踩数量
     */
    private Integer dislikes;
    /**
     * 最新点赞的用户(2个)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> likeUserIds;
    /**
     * 是否已读
     */
    private Integer readFlag;
    /**
     * 忽略通知
     */
    private Integer hidden;
    /**
     * 最近被点赞时间
     */
    private Date likeTime;
}