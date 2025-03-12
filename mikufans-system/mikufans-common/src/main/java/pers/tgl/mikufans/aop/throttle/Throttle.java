package pers.tgl.mikufans.aop.throttle;

import java.lang.annotation.*;
import java.time.temporal.ChronoUnit;

/**
 * 对接口进行限流和熔断 (会要求用户登录)
 * 防止用户反复提交无用数据
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Throttle {
    /**
     * 在单位时间内最多提交多少次
     */
    int limit() default 10;
    /**
     * 持续时间
     */
    int duration() default 3;
    /**
     * 时间单位
     */
    ChronoUnit timeUnit() default ChronoUnit.MINUTES;
    /**
     * 拒绝的提示
     */
    String message() default "提交过于频繁,请稍后再试！";
    /**
     * 额外补充redis的key EL表达式
     */
    String key() default "";
}