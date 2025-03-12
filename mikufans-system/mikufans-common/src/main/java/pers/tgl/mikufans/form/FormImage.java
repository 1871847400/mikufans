package pers.tgl.mikufans.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface FormImage {
    /**
     * 图片宽度
     */
    String width() default "auto";
    /**
     * 图片高度
     */
    String height() default "auto";
    /**
     * 圆角
     */
    String radius() default "none";
}