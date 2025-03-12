package pers.tgl.mikufans.validator.role;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 需要拥有任意一个角色,才能修改此属性
 */
@Documented
@Constraint(validatedBy = { RoleValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Deprecated
public @interface Role {
    /**
     * 允许的角色列表
     */
    String[] value() default {};
    /**
     * 忽略哪些值,这些值不检测权限
     */
    String[] ignores() default {};

    String message() default "没有权限设置此参数";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {};
}