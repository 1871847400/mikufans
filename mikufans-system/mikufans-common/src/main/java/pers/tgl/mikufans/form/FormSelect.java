package pers.tgl.mikufans.form;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为下拉框专门准备的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface FormSelect {
    /**
     * 下拉框选项列表来源
     */
    @JsonIgnore
    Class<? extends OptionsProvider> provider() default OptionsProvider.class;
    /**
     * 可以手动输入值然后下拉选择
     */
    boolean filterable() default false;
    /**
     * 是否可以手动创建新选项,filterable必须为true
     */
    boolean allowCreate() default false;
    /**
     * 默认选择第一项
     */
    boolean defaultFirstOption() default false;
    /**
     * 空值设置
     * 默认会将null,undefined,''当做空值
     */
    String[] empty() default {};
    /**
     * 是否可以多选
     */
    boolean multiple() default false;
    /**
     * 多选个数限制 0为不限制
     */
    int multipleLimit() default 0;
}