package pers.tgl.mikufans.aop.login;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.util.RedisUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class LoginLimitAspect {
    private static final String REDIS_KEY_PREFIX = "login:limit:";
    private final RedisUtils redisUtils;

    @Pointcut("@annotation(pers.tgl.mikufans.aop.login.LoginLimit)")
    public void pointCut() {};

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        LoginLimit loginLimit = method.getAnnotation(LoginLimit.class);
        StandardEvaluationContext context = new StandardEvaluationContext();
        //将前端传入的每个参数放入上下文中
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            context.setVariable(parameters[i].getName(), joinPoint.getArgs()[i]);
        }
        //计算表达式后的结果
        Object value = new SpelExpressionParser().parseExpression(loginLimit.value()).getValue(context);
        String key = REDIS_KEY_PREFIX + "username:" + value;
        int count = Integer.parseInt(redisUtils.getString(key, "0"));
        Duration lockTime = Duration.ofSeconds(loginLimit.lockTime());
        if (count >= loginLimit.tryCount()) {
            Long ttl = redisUtils.ttl(key, TimeUnit.SECONDS);
            throw new CustomException("登录过于频繁,请稍等" + ttl + "秒");
        }
        try {
            Object result = joinPoint.proceed();
            //登录成功清除失败记录
            redisUtils.del(key);
            return result;
        } catch (Exception e) {
            for (Class<? extends Exception> exceptionClass : loginLimit.exception()) {
                if (exceptionClass.isAssignableFrom(e.getClass())) {
                    redisUtils.increment(key, 1);
                    redisUtils.expire(key, lockTime);
                    break;
                }
            }
            throw e;
        }
    }
}