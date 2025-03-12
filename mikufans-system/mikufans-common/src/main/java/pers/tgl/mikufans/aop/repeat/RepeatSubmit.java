package pers.tgl.mikufans.aop.repeat;

import java.lang.annotation.*;

/**
 * 防重复提交,依据方法，参数以及用户id进行区分判断
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {
    /**
     * 关闭检测
     */
    boolean disable() default false;
    /**
     * 间隔：毫秒
     */
    long interval() default 250;
}