package pers.tgl.mikufans.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表单字段设置
 * 便于前端生成控件
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface FormItem {
    /**
     * 表单类型
     */
    FormItemType type();
    /**
     * form-item label
     */
    String label();
    /**
     * 未输入/选择时的提示，也将用于validate时的错误提示
     */
    String placeholder() default "";
    /**
     * 前端验证该字段是否必须要,并且显示红点
     */
    Scope required() default Scope.NONE;
    /**
     * 最大字数限制  文本类型才有效
     */
    int maxlength() default -1;
    /**
     * 时间显示格式(必须使用前端的格式) datetime类型才有效
     */
    String timeFormat() default "YYYY-MM-DD HH:mm:ss";
    /**
     * 文本域行数
     */
    int rows () default 3;
    /**
     * 数字类型 最小值
     */
    int min() default Integer.MIN_VALUE;
    /**
     * 数字类型 最大值
     * 注意 JS 中Number的最大值 大于int 小于long
     */
    int max() default Integer.MAX_VALUE;
    /**
     * 是否禁用
     */
    Scope disabled() default Scope.NONE;
    /**
     * 是否只读
     */
    Scope readonly() default Scope.NONE;
    /**
     * 默认值(EL表达式) 支持部分函数以及该注解内的参数,例如访问: #type
     * @see FormFunction
     */
    String value() default "Null";
    /**
     * 字典类型
     * 当表单类型为下拉或单选时有效
     */
    String dict() default "";
}