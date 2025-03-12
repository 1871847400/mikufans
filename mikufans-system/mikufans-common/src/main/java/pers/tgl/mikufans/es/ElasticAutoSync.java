package pers.tgl.mikufans.es;

import com.github.yulichang.toolkit.Constant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在es文档类上,定期检查所有表的更新时间，然后自动同步到ES中
 * 如果主表逻辑删除了,也会在es中删除，如果物理删除则es中不会被删
 *
 * 文档类需要提供一个构造方法：参数是：主表,关联表1,关联表2...
 * 如果其中一张表的实体没有找到，不会传递null作为参数，而是调用空参方法生成的对象
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ElasticAutoSync {
    /**
     * 同步任务名称,如果要对一个索引开启多个任务,需要设置不同的名称
     */
    String name() default "";
    /**
     * 主表
     */
    Class<?> tableClass();
    /**
     * 关联表列表
     */
    Join[] joins() default {};
    /**
     * 间隔多久(秒)
     */
    long fixedDelay() default 10;

    @interface Join {
        /**
         * 连接发起表 默认主表
         */
        Class<?> thisTable() default Object.class;
        /**
         * 当前表字段 默认主键
         * 请使用lombok的Fields当做字段名称,会通过mp去找真实sql字段名称
         */
        String thisField() default "";
        /**
         * 连接哪张表
         */
        Class<?> joinTable();
        /**
         * 连接哪个字段，默认主键
         */
        String joinField() default "";
        /**
         * 连接方式 left right inner full
         * @see Constant
         */
        String method() default Constant.LEFT_JOIN;
    }
}