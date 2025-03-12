package pers.tgl.mikufans.domain.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.consts.TransferMode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.form.Form;
import pers.tgl.mikufans.form.FormItem;
import pers.tgl.mikufans.form.FormItemType;
import pers.tgl.mikufans.form.Scope;
import pers.tgl.mikufans.jackson.sensitive.Sensitive;

@EqualsAndHashCode(callSuper = true)
@Data
@PermFlag(name = "图片资源", group = PermGroup.BUSINESS)
@Form(labelWidth = "100px", labelPosition = "right")
public class ImageResource extends UserBaseEntity {
    /**
     * 原始图片的哈希值
     */
    @FormItem(type = FormItemType.TEXT, label = "MD5", disabled = Scope.BOTH)
    private String md5;
    /**
     * 本地保存位置
     */
    @Sensitive
    @FormItem(type = FormItemType.TEXT, label = "本地路径", placeholder = "请输入本地路径")
    private String localPath;
    /**
     * 转存模式
     */
    @Sensitive
    private TransferMode transferMode;
    /**
     * 转储地址
     */
    @Sensitive
    @FormItem(type = FormItemType.TEXT, label = "转存路径", placeholder = "请输入转存路径")
    private String transferPath;
    /**
     * 媒体类型,如: image/jpg
     */
    private String mediaType;
    /**
     * 文件大小(字节)
     */
    private Integer fileSize;
    /**
     * 主色调(颜色代码)
     */
    @FormItem(type = FormItemType.TEXT, label = "主色调", placeholder = "请输入主色调")
    private String mainColor;
    /**
     * 分辨率
     */
    private String resolution;
}