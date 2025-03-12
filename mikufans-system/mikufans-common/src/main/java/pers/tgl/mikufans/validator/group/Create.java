package pers.tgl.mikufans.validator.group;

import javax.validation.groups.Default;

/**
 * 所有验证器默认分组为: Default
 * 继承Default后,没有指定groups的验证器也将生效
 *
 * Example:
 * Controller.java
 *      @Validated(groups=Create) model
 * Model.java (以下两种规则都将生效)
 *      @NotNull
 *      @NotNull(groups=Create)
 */
public interface Create extends Default {
}