package pers.tgl.mikufans.domain.system;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.bangumi.Bangumi;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.domain.enums.CarouselPosition;
import pers.tgl.mikufans.domain.provider.ChannelProvider;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.form.*;
import pers.tgl.mikufans.validator.group.Create;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@PermFlag(name = "轮播图", group = PermGroup.SYSTEM)
@Form(name = "轮播图", labelWidth = "110px", labelPosition = "right")
public class SysCarousel extends SystemBaseEntity {
    /**
     * 播放位置
     */
    @NotNull(message = "请选择播放位置", groups = Create.class)
    @FormItem(type = FormItemType.SELECT, label = "播放位置", placeholder = "请选择播放位置", required = Scope.BOTH, dict = "carousel_position")
    private CarouselPosition position;
    /**
     * 频道id
     * 需要播放位置为 channel
     */
    @FormItem(type = FormItemType.SELECT, label = "频道分区", placeholder = "请选择频道分区")
    @FormSelect(provider = ChannelProvider.class, empty = "0")
    private Long channelId;
    /**
     * 封面id
     */
    @NotNull(message = "请选择封面", groups = Create.class)
    @FormItem(type = FormItemType.IMAGE, label = "封面", placeholder = "请选择封面", required = Scope.BOTH)
    @FormImage(width = "150px")
    private Long bannerId;
    /**
     * 主色调
     * 不要直接关联图片,不方便调整
     */
    @FormItem(type = FormItemType.COLOR, label = "主色调", placeholder = "请选择主色调", required = Scope.BOTH)
    private String mainColor;
    /**
     * 预览缩略图id
     */
    @FormItem(type = FormItemType.IMAGE, label = "缩略图")
    @FormImage(width = "150px")
    private Long thumbnailId;
    /**
     * 主标题
     */
    @NotBlank(message = "请输入标题", groups = Create.class)
    @Length(max = 32, message = "标题太长了")
    @FormItem(type = FormItemType.TEXT, label = "标题", placeholder = "请输入标题", maxlength = 32, required = Scope.BOTH)
    private String title;
    /**
     * 副标题
     */
    @FormItem(type = FormItemType.TEXT, label = "副标题", placeholder = "请输入副标题", maxlength = 32)
    @Length(max = 32, message = "副标题太长了")
    private String subtitle;
    /**
     * 跳转链接
     */
    @NotBlank(message = "请输入跳转链接", groups = Create.class)
    @FormItem(type = FormItemType.TEXT, label = "跳转链接", placeholder = "请输入跳转链接", required = Scope.BOTH)
    private String url;
    /**
     * 是否禁用 0:正常 1:禁用
     */
    @FormItem(type = FormItemType.RADIO, label = "是否禁用", placeholder = "请选择是否禁用", value = "0", required = Scope.BOTH, dict = "is_status")
    private Integer disabled;
    /**
     * 视频id
     */
    @FormItem(type = FormItemType.TEXT, label = "视频id", placeholder = "请输入视频id")
    private Long videoId;
    /**
     * 播放开始日期
     */
    @FormItem(type = FormItemType.DATE, label = "开始时间", placeholder = "请输入开始时间", value = "now()")
    private Date startDate;
    /**
     * 播放结束日期
     */
    @Nullable
    @FormItem(type = FormItemType.DATE, label = "结束时间", placeholder = "请输入结束时间")
    private Date endDate;
    /**
     * 排序
     */
    @FormItem(type = FormItemType.NUMBER, label = "排序", placeholder = "请输入排序值", value = "1", min = 0)
    @Min(0)
    private Integer sort;
    /**
     * 关联的视频
     */
    @TableField(exist = false)
    private Video video;
    /**
     * 关联的节目
     */
    @TableField(exist = false)
    private Bangumi bangumi;
}