package pers.tgl.mikufans.domain.video;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UniqueElements;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.enums.VideoType;
import pers.tgl.mikufans.form.*;
import pers.tgl.mikufans.validator.group.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 视频表
 * @TableName video
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="video", autoResultMap = true)
@FieldNameConstants
@Data
@PermFlag(name = "视频管理", group = PermGroup.BUSINESS)
@Form(name = "视频")
public class Video extends UserBaseEntity {
    /**
     * 6位短id
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String sid;
    /**
     * uid(创建序号)
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Integer uid;
    /**
     * 视频类型  后续无法再次修改
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private VideoType type;
    /**
     * 分区id
     */
    @NotNull(groups = Create.class)
    @FormItem(type = FormItemType.TEXT, label = "频道分区", placeholder = "请选择分区", required = Scope.BOTH)
    private Long channelId;
    /**
     * 视频标题
     */
    @TableField(condition = SqlCondition.LIKE_RIGHT)
    @NotBlank(message = "请输入视频标题", groups = Create.class)
    @FormItem(type = FormItemType.TEXT, label = "标题", placeholder = "请输入标题", required = Scope.BOTH, maxlength = 32)
    private String title;
    /**
     * 视频简介
     */
    @FormItem(type = FormItemType.TEXTAREA, label = "简介", placeholder = "请输入简介", maxlength = 32)
    @TableField(whereStrategy = FieldStrategy.NEVER)
    private String intro;
    /**
     * 标签列表
     */
    @UniqueElements(message = "请不要使用重复标签")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<@NotBlank String> tags;
    /**
     * 0非转载 1是转载
     */
    @NotNull(groups = Create.class, message = "请选择是否转载")
    @Range(min = 0, max = 1)
    @FormItem(type = FormItemType.RADIO, label = "是否转载", placeholder = "请选择是否转载", dict = "is_status", required = Scope.BOTH)
    private Integer republish;
    /**
     * 转载地址
     */
    @FormItem(type = FormItemType.TEXT, label = "转载地址", placeholder = "请输入转载地址")
    private String sourceUrl;
    /**
     * 0:不能被搜到 1:可以被搜到
     */
    private Integer search;
    /**
     * 观看等级
     */
    @Range(min = 0, max = 6)
    private Integer userLevel;
    /**
     * 电脑端封面id 16:9
     */
    @FormItem(type = FormItemType.IMAGE, label = "PC封面", placeholder = "请选择PC封面", required = Scope.BOTH)
    @FormImage(width = "100px", height = "56.25px")
    private Long bannerId;
    /**
     * 手机端封面id 4:3
     */
    @FormItem(type = FormItemType.IMAGE, label = "手机封面", placeholder = "请选择手机封面")
    @FormImage(width = "100px", height = "75px")
    private Long mBannerId;
    /**
     * 所有视频的总时长(ms)
     */
    private Integer duration;
    /**
     * 节目id
     */
    private Long bangumiId;
    /**
     * 热度分数(可能为负数)
     */
    private BigDecimal score;
    /**
     * 播放数量
     */
    private Integer plays;
    /**
     * 投币数量
     */
    private Integer coins;
    /**
     * 收藏数量
     */
    private Integer stars;
    /**
     * 弹幕数量
     */
    private Integer danmus;
    /**
     * 分享次数
     */
    private Integer shares;
    /**
     * 是否被禁用 0:否 1:是
     * 一般为被举报下架后的情况
     */
    private Integer disabled;
    /**
     * 被禁用的原因
     */
    private String reason;
    /**
     * 重审次数,下架后重新申请检查
     */
    private Integer apply;
    /**
     * 访问地址
     */
    public String getUri() {
        return "/video/" + getSid();
    }
    /**
     * 类型标签
     */
    public String getTypeTag() {
        return type != null ? type.getTag() : "";
    }
}