package pers.tgl.mikufans.domain.video;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.enums.DanmuType;
import pers.tgl.mikufans.form.Form;
import pers.tgl.mikufans.form.FormItem;
import pers.tgl.mikufans.form.FormItemType;
import pers.tgl.mikufans.form.Scope;
import pers.tgl.mikufans.handler.type.ColorTypeHandler;
import pers.tgl.mikufans.jackson.sensitive.Sensitive;
import pers.tgl.mikufans.jackson.sensitive.SensitiveType;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 视频弹幕表
 * @TableName video_danmu
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="video_danmu", autoResultMap = true)
@FieldNameConstants
@Data
@PermFlag(name = "视频弹幕", group = PermGroup.BUSINESS)
@Form(name = "弹幕", labelPosition = "right", labelWidth = "100px")
public class VideoDanmu extends UserBaseEntity {
    /**
     * 视频id
     */
    @FormItem(type = FormItemType.TEXT, label = "视频id", placeholder = "请输入视频id", required = Scope.BOTH)
    private Long videoId;
    /**
     * 视频分集id
     */
    @FormItem(type = FormItemType.TEXT, label = "分集id", placeholder = "请输入分集id", required = Scope.BOTH)
    private Long partId;
    /**
     * 弹幕内容
     */
    @Sensitive(SensitiveType.TERM)
    @FormItem(type = FormItemType.TEXT, label = "弹幕内容", placeholder = "请输入弹幕内容", required = Scope.BOTH)
    private String content;
    /**
     * 颜色代码 16进制格式: #ffffff  #fff
     * 数据库实际存储格式为10进制：16777215
     */
    @FormItem(type = FormItemType.COLOR, label = "颜色代码", placeholder = "请输入颜色代码", required = Scope.BOTH, value = "#fff")
    @TableField(typeHandler = ColorTypeHandler.class)
    private String fontColor;
    /**
     * 字体类型
     */
    private Integer fontType;
    /**
     * 发送时间 秒
     */
    @FormItem(type = FormItemType.NUMBER, label = "显示时间", placeholder = "请输入显示时间", required = Scope.BOTH)
    private BigDecimal sendTime;
    /**
     * 弹幕类型
     */
    @FormItem(type = FormItemType.RADIO, label = "弹幕类型", placeholder = "请选择弹幕类型", required = Scope.BOTH, dict = "danmu_type", value = "1")
    private DanmuType danmuType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getPublishDate() {
        return getCreateTime();
    }

    public String getUri() {
        return "/video/" + getVideoId() + "#time" + getSendTime();
    }
}