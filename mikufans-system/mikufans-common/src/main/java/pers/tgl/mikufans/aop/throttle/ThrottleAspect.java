package pers.tgl.mikufans.aop.throttle;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.util.RedisUtils;
import pers.tgl.mikufans.util.SecurityUtils;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ThrottleAspect {
    private final RedisUtils redisUtils;

    @Pointcut("@annotation(pers.tgl.mikufans.aop.throttle.Throttle)")
    public void pointCut() {};

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Throttle throttle = signature.getMethod().getAnnotation(Throttle.class);
        Long contextUserId = SecurityUtils.getContextUserId(true);
        //方法全局唯一标记
        String methodPath = signature.toLongString();
        String redisKey = "throttle:" + contextUserId + ":" + methodPath;
        if (StrUtil.isNotBlank(throttle.key())) {
            MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                    joinPoint.getTarget(), signature.getMethod(), joinPoint.getArgs(), new DefaultParameterNameDiscoverer());
            try {
                Object value = new SpelExpressionParser().parseExpression(throttle.key()).getValue(context);
                if (value != null) {
                    redisKey += ":" + value;
                }
            } catch (Exception e) {
                log.error("EL表达式错误", e);
            }
        }
        Long count = redisUtils.increment(redisKey, 1);
        if (count != null) {
            if (count == 1) {
                Duration duration = Duration.of(throttle.duration(), throttle.timeUnit());
                redisUtils.expire(redisKey, duration);
            }
            if (count > throttle.limit()) {
                throw new CustomException(throttle.message());
            }
        }
        return joinPoint.proceed();
    }
}