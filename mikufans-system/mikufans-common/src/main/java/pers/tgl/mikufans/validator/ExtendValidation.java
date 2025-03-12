package pers.tgl.mikufans.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 伪继承其它类的校验规则,只能放在对象上,不能放在原始对象中
 * 将此对象属于属性复制到指定的类上,然后进行校验
 * 需要有其它类的相同字段才行
 * 只支持字段，不支持getter
 *
 * 校验组也会继承过去
 *
 * 注意：小心继承包含@NotNull标记的字段，而注解类却没有
 */
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtendValidation {
    Class<?>[] value();
}