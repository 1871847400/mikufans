package pers.tgl.mikufans.domain.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.domain.provider.ReportBehaviorProvider;
import pers.tgl.mikufans.form.*;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@PermFlag(name = "举报类别", group = PermGroup.SYSTEM)
@Form(labelWidth = "90px", labelPosition = "right", labelSuffix = ":")
public class ReportBehavior extends SystemBaseEntity {
    /**
     * 行为分类
     */
    @NotBlank(message = "请输入分类名称")
    @FormItem(type = FormItemType.SELECT, label = "行为分类", placeholder = "请输入分类名称", required = Scope.BOTH, maxlength = 32)
    @FormSelect(provider = ReportBehaviorProvider.class, filterable = true, allowCreate = true)
    private String category;
    /**
     * 行为名称
     */
    @NotBlank(message = "请输入行为名称")
    @FormItem(type = FormItemType.TEXT, label = "行为名称", placeholder = "请输入行为名称", required = Scope.BOTH, maxlength = 32)
    private String behavior;
    /**
     * 行为介绍
     */
    @FormItem(type = FormItemType.TEXTAREA, label = "行为介绍", placeholder = "请输入行为介绍", maxlength = 64)
    private String intro;
}