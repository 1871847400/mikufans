package pers.tgl.mikufans.domain.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.form.Form;
import pers.tgl.mikufans.form.FormItem;
import pers.tgl.mikufans.form.FormItemType;
import pers.tgl.mikufans.form.Scope;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@PermFlag(name = "系统参数", group = PermGroup.SYSTEM)
@Form(name = "系统参数", labelPosition = "right")
public class SysParam extends SystemBaseEntity {
    /**
     * 参数键
     */
    @NotBlank(message = "请输入参数键")
    @FormItem(type = FormItemType.TEXT, label = "参数键", placeholder = "请输入参数键", required = Scope.BOTH, maxlength = 64)
    private String paramKey;
    /**
     * 参数值
     */
    @NotNull(message = "请输入参数值")
    @FormItem(type = FormItemType.TEXT, label = "参数值", placeholder = "请输入参数值", required = Scope.BOTH, maxlength = 256)
    private String paramValue;
}