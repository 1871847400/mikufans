package pers.tgl.mikufans.domain.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.form.*;
import pers.tgl.mikufans.validator.group.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@PermFlag(name = "扩展词库", group = PermGroup.SYSTEM)
@Form(name = "扩展词库", labelWidth = "90px", labelPosition = "right")
public class SysExtDict extends SystemBaseEntity {
    /**
     * 词条内容
     */
    @NotBlank(message = "请输入词条内容", groups = Create.class)
    @FormItem(type = FormItemType.TEXT, label = "词条内容", placeholder = "请输入词条内容", required = Scope.BOTH, maxlength = 32)
    @Length(max = 32)
    private String term;
    /**
     * 是否为敏感词 0:否 1:是
     */
    @NotNull(message = "请选择是否为敏感词", groups = Create.class)
    @Range(min = 0, max = 1)
    @FormItem(type = FormItemType.RADIO, label = "敏感词", placeholder = "请选择是否为敏感词", required = Scope.BOTH, dict = "is_status")
    private Integer illegal;
}