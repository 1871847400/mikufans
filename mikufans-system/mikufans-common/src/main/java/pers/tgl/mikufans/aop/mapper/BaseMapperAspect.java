package pers.tgl.mikufans.aop.mapper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

//@Aspect
//@Component
public class BaseMapperAspect {
    /**
     * 监听容器内mapper内的所有方法执行
     * 注意：通过SQLHelper.getMapper拿到的无法监听，也就是Db工具类
     */
    @Pointcut("execution(* pers.tgl.mikufans.mapper..*.*(..))")
    public void pointCut() {}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        System.out.println(method.getName() + "执行了");
        return joinPoint.proceed();
    }
}