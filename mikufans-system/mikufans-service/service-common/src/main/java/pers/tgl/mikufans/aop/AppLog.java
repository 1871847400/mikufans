package pers.tgl.mikufans.aop;

import pers.tgl.mikufans.domain.user.UserOperLog;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AppLog {
    /**
     * 操作标题
     */
    String value();
    /**
     * 操作类型
     */
    int type() default UserOperLog.OPER_TYPE_NORMAL;
}