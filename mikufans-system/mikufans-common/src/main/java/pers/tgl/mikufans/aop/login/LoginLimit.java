package pers.tgl.mikufans.aop.login;

import org.springframework.security.core.AuthenticationException;

import java.lang.annotation.*;

/**
 * 登录方法防暴力破解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginLimit {
    /**
     * spring el表达式,用于key
     */
    String value();
    /**
     * 最大尝试次数
     */
    int tryCount() default 5;
    /**
     * 次数用光后锁定 账号和IP 一段时间 单位秒
     */
    int lockTime() default 60;
    /**
     * 捕获的异常对象,该异常及其子类才算作登录失败
     */
    Class<? extends Exception>[] exception() default { AuthenticationException.class };
}