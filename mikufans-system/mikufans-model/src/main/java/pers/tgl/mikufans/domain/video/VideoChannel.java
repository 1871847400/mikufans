package pers.tgl.mikufans.domain.video;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.domain.provider.ChannelProvider;
import pers.tgl.mikufans.form.*;
import pers.tgl.mikufans.jackson.sensitive.Sensitive;
import pers.tgl.mikufans.validator.group.Create;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 视频分区表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@PermFlag(name = "频道分区", group = PermGroup.BUSINESS)
@Form(name = "频道分区", labelWidth = "100px", labelPosition = "right")
public class VideoChannel extends SystemBaseEntity {
    /**
     * 父分区id
     */
    @FormItem(type = FormItemType.SELECT, label = "父分区", placeholder = "请选择父分区(非必须)")
    @FormSelect(provider = ChannelProvider.class, empty = "0")
    private Long pid;
    /**
     * 分区名称
     */
    @FormItem(type = FormItemType.TEXT, label = "分区名称", placeholder = "请输入分区名称", required = Scope.BOTH, maxlength = 32)
    @NotBlank(message = "请输入分区名称", groups = Create.class)
    private String channelName;
    /**
     * 分区描述
     */
    @FormItem(type = FormItemType.TEXTAREA, label = "分区描述", placeholder = "请输入分区描述", maxlength = 64)
    private String channelDesc;
    /**
     * 图标名称
     */
    @FormItem(type = FormItemType.ICON, label = "图标名称", placeholder = "请选择图标")
    private String iconName;
    /**
     * 跳转路径
     */
    @FormItem(type = FormItemType.TEXT, label = "跳转链接", placeholder = "请输入跳转链接")
    private String url;
    /**
     * 是否禁用 0启用 1禁用
     */
    @Sensitive
    @FormItem(type = FormItemType.RADIO, label = "是否禁用", placeholder = "请选择是否禁用", value = "0", dict = "is_status")
    private Integer disabled;
    /**
     * 排序值
     */
    @Min(value = 0, message = "排序值最小0")
    @FormItem(type = FormItemType.NUMBER, label = "排序", placeholder = "请输入排序值", value = "1", min = 0)
    private Integer sort;
    /**
     * 子分区列表
     */
    @TableField(exist = false)
    private List<VideoChannel> children;
}