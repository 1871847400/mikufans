package pers.tgl.mikufans.domain.system;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.form.Form;
import pers.tgl.mikufans.form.FormItem;
import pers.tgl.mikufans.form.FormItemType;
import pers.tgl.mikufans.form.Scope;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@PermFlag(name = "字典类型", group = PermGroup.SYSTEM)
@Form(name = "字典类型", labelWidth = "100px", labelPosition = "right")
@TableName("sys_dict_type")
public class SysDictType extends SystemBaseEntity {
    @FormItem(type = FormItemType.TEXT, label = "字典名称", placeholder = "请输入字典名称", required = Scope.BOTH, maxlength = 32)
    @NotBlank(message = "请输入字典名称")
    @Length(max = 32, message = "字典名称太长了")
    private String dictName;

    @FormItem(type = FormItemType.TEXT, label = "字典类型", placeholder = "请输入字典类型", required = Scope.BOTH, maxlength = 32)
    @NotBlank(message = "请输入字典类型")
    @Length(max = 32, message = "字典类型太长了")
    private String dictType;

    /**
     * 是否可以手动增加/减少字典数据
     * 主要用于一些需要在程序内进行逻辑判断的字典类型，如：业务类型不能随意改变
     * 而性别这种仅为展示的数据则可以自由调整
     */
    @Range(min = 0, max = 1)
    private Integer mutable;
}