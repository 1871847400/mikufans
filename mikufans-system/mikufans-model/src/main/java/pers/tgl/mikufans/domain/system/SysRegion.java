package pers.tgl.mikufans.domain.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.form.Form;
import pers.tgl.mikufans.form.FormItem;
import pers.tgl.mikufans.form.FormItemType;
import pers.tgl.mikufans.form.Scope;
import pers.tgl.mikufans.validator.group.Create;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@PermFlag(name = "地区语言", group = PermGroup.SYSTEM)
@Form(labelWidth = "120px", labelPosition = "right")
public class SysRegion extends SystemBaseEntity {
    /**
     * 地区名称
     */
    @FormItem(type = FormItemType.TEXT, label = "地区名称", placeholder = "请输入地区名称", required = Scope.BOTH, maxlength = 16)
    @Length(max = 16, message = "地区名称太长了")
    @NotBlank(groups = Create.class, message = "请输入地区名称")
    private String regionName;
    /**
     * 地区代码
     */
    @FormItem(type = FormItemType.TEXT, label = "地区代码", placeholder = "请输入地区代码", required = Scope.BOTH, maxlength = 32)
    @Length(max = 32, message = "地区代码太长了")
    @NotBlank(groups = Create.class, message = "请输入地区代码")
    private String regionCode;
    /**
     * 语言名称
     */
    @FormItem(type = FormItemType.TEXT, label = "语言名称", placeholder = "请输入语言名称", required = Scope.BOTH, maxlength = 16)
    @Length(max = 16, message = "语言名称太长了")
    @NotBlank(groups = Create.class, message = "请输入语言名称")
    private String langName;
    /**
     * 语言代码
     */
    @FormItem(type = FormItemType.TEXT, label = "语言代码", placeholder = "请输入语言代码", required = Scope.BOTH, maxlength = 32)
    @Length(max = 32, message = "语言代码太长了")
    @NotBlank(groups = Create.class, message = "请输入语言代码")
    private String langCode;
    /**
     * 语言英文名称
     */
    @FormItem(type = FormItemType.TEXT, label = "语言名称(英文)", placeholder = "请输入语言英文名称", required = Scope.BOTH, maxlength = 24)
    @Length(max = 24, message = "语言英文名称太长了")
    @NotBlank(groups = Create.class, message = "请输入语言英文名称")
    private String langEnName;
}