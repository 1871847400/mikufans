package pers.tgl.mikufans.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Form {
    /**
     * 实体名称，会决定dialog的标题,如：新增用户/编辑用户
     */
    String name() default "";
    /**
     * left right top
     */
    String labelPosition() default "left";

    String labelWidth() default "80px";

    String labelSuffix() default ":";
}