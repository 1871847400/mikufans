package pers.tgl.mikufans.jackson.sensitive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 敏感字段,jackson序列化时进行脱敏，仅生效于注册了模块的objectMapper
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Sensitive {
    /**
     * 脱敏方法
     */
    SensitiveType value() default SensitiveType.CLEAR_TO_NULL;
    /**
     * 如果当前账号为管理员则跳过脱敏
     */
    boolean skipIfAdmin() default true;
}