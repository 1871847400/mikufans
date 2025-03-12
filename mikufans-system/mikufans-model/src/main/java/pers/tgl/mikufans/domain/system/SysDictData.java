package pers.tgl.mikufans.domain.system;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.form.Form;
import pers.tgl.mikufans.form.FormItem;
import pers.tgl.mikufans.form.FormItemType;
import pers.tgl.mikufans.form.Scope;
import pers.tgl.mikufans.validator.group.Create;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@PermFlag(name = "字典数据", group = PermGroup.SYSTEM)
@Form(name = "字典数据", labelWidth = "100px", labelPosition = "right")
@TableName("sys_dict_data")
public class SysDictData extends SystemBaseEntity {
    /**
     * 外键关联字典类型
     */
    @FormItem(type = FormItemType.TEXT, label = "字典类型", placeholder = "请输入字典类型", required = Scope.BOTH, maxlength = 32, disabled = Scope.BOTH)
    @NotBlank(message = "请选择字典类型", groups = Create.class)
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String dictType;

    @FormItem(type = FormItemType.TEXT, label = "字典标签", placeholder = "请输入字典标签", required = Scope.BOTH, maxlength = 32)
    @NotBlank(message = "请输入字典标签", groups = Create.class)
    @Length(max = 32, message = "字典标签太长了")
    private String dictLabel;

    @FormItem(type = FormItemType.TEXT, label = "字典键值", placeholder = "请输入字典键值", required = Scope.BOTH, maxlength = 32)
    @NotBlank(message = "请输入字典键值", groups = Create.class)
    @Length(max = 32, message = "字典键值太长了")
    private String dictValue;

    @FormItem(type = FormItemType.NUMBER, label = "排序值", placeholder = "请输入排序值", required = Scope.BOTH, value = "1", min = 0)
    @Min(0)
    @OrderBy(asc = true)
    private Integer dictSort;

    @FormItem(type = FormItemType.RADIO, label = "默认选项", required = Scope.BOTH, dict = "is_status", value = "0")
    @Range(min = 0, max = 1)
    private Integer defFlag;

    @FormItem(type = FormItemType.RADIO, label = "是否禁用", required = Scope.BOTH, dict = "is_status", value = "0")
    @Range(min = 0, max = 1)
    private Integer disabled;

    @FormItem(type = FormItemType.SELECT, label = "标签类型", placeholder = "请选择标签类型", dict = "tag_type")
    private String tagType;

    @FormItem(type = FormItemType.ICON, label = "图标名称", placeholder = "请选择图标名称")
    private String iconName;

    @FormItem(type = FormItemType.COLOR, label = "图标颜色", placeholder = "请输入图标颜色代码")
    private String iconColor;
}