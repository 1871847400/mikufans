package pers.tgl.mikufans.aop.repeat;

import cn.hutool.crypto.digest.DigestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.util.*;

import javax.annotation.Resource;

/**
 * 创建切面类防止用户重复提交
 */
@Aspect
@Component
public class RepeatSubmitAspect {
    @Resource
    private RedisUtils redisUtils;

//    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
//    public void classPointCut() {}

    @Pointcut("@annotation(pers.tgl.mikufans.aop.repeat.RepeatSubmit)")
    public void pointCut() {};

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RepeatSubmit repeatSubmit = signature.getMethod().getAnnotation(RepeatSubmit.class);
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (repeatSubmit == null || repeatSubmit.disable() || contextUserId == null) {
            return joinPoint.proceed();
        }
        //public pers.tgl.testaop.User pers.tgl.testaop.controller.TestController.test4(pers.tgl.testaop.User,java.lang.Integer)
        //方法全局唯一标记
        String methodPath = signature.toLongString();
        //方法的参数,包括params和body
        StringBuilder methodArgs = new StringBuilder();
        for (Object arg : joinPoint.getArgs()) {
            if (arg != null) {
                methodArgs.append(JsonUtils.writeString(arg));
            }
        }
        //参数会非常长,必须缩减到固定长度
        String key = "REPEAT:" + DigestUtil.md5Hex(contextUserId + methodPath + methodArgs);
        //如果不存在才会设置成功
        boolean success = redisUtils.setNX(key, System.currentTimeMillis(), repeatSubmit.interval());
        if (!success) {
            throw new CustomException(Code.REPEAT_SUBMIT);
        }
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            //如果目标方法执行失败.则不记录
            redisUtils.del(key);
            throw e;
        }
    }
}