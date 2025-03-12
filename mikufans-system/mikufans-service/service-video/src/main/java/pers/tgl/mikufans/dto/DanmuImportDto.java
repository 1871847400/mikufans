package pers.tgl.mikufans.dto;

import lombok.Data;
import pers.tgl.mikufans.form.Form;
import pers.tgl.mikufans.form.FormItem;
import pers.tgl.mikufans.form.FormItemType;
import pers.tgl.mikufans.form.Scope;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Form(name = "导入弹幕", labelWidth = "100px", labelPosition = "right")
public class DanmuImportDto implements Serializable {
    /**
     * 目标视频
     */
    @NotNull
    @FormItem(type = FormItemType.TEXT, label = "分集id", placeholder = "请输入分集id", required = Scope.BOTH)
    private Long partId;
    /**
     * 使用该用户来创建弹幕
     */
    @NotNull
    @FormItem(type = FormItemType.TEXT, label = "用户id", placeholder = "请输入用户id", required = Scope.BOTH)
    private Long userId;
    /**
     * 弹幕来源
     */
    @NotBlank
    @FormItem(type = FormItemType.TEXT, label = "XML链接", placeholder = "请输入XML链接", required = Scope.BOTH)
    private String url;
}