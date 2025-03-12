package pers.tgl.mikufans.es;

import java.lang.annotation.*;

/**
 * 用于ES同步
 * @deprecated 不好处理一张表同时关联多张表, 关联表增加字段上的class也要增加
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Inherited
public @interface JoinField {
    /**
     * 关联哪张表
     */
    Class<?> value();
    /**
     * join目标表哪个字段 默认是主键
     * 使用 Fields.xxx 会通过TableInfo自动找到映射的数据库字段
     */
    String joinField() default "";
}