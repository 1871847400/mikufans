package pers.tgl.mikufans.aop;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.domain.user.UserOperLog;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.util.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;

/**
 * 日志记录切面
 * 优先级设置为最高,也就是最先执行
 */
@Aspect
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class AppLogAspect {
    @Pointcut("@annotation(pers.tgl.mikufans.aop.AppLog)")
    public void pointCut() {};

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AppLog appLog = signature.getMethod().getAnnotation(AppLog.class);
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setTitle(appLog.value());
        userOperLog.setOperType(appLog.type());
        UserToken userToken = SecurityUtils.getContextUserToken();
        if (userToken == null) {
            userOperLog.setUserId(0L);
            userOperLog.setUserType(0);
            userOperLog.setCreateBy(0L);
            userOperLog.setUsername("");
            if (appLog.type() == UserOperLog.OPER_TYPE_LOGIN) {
                Object[] args = joinPoint.getArgs();
                if (args.length > 0) {
                    userOperLog.setReqParams(JsonUtils.writeString(args[0]));
                }
            }
        } else {
            userOperLog.setUserId(userToken.getId());
            userOperLog.setUserType(userToken.getUserType());
            userOperLog.setUsername(userToken.getUsername());
        }
        HttpServletRequest request = ServletUtils.getRequest();
        userOperLog.setIpaddr(IpUtils.getIpaddr(request));
        userOperLog.setMethodPath(signature.toLongString());
        userOperLog.setReqParams(request.getQueryString());
        userOperLog.setReqMethod(request.getMethod());
        userOperLog.setReqUri(ServletUtils.getRequest().getRequestURI());
        userOperLog.setUserAgent(request.getHeader("User-Agent"));
        userOperLog.setClientType(MyUtils.getDeviceType(request));
        Instant start = Instant.now();
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            //设置执行状态
            userOperLog.setOperStatus(1);
            throw throwable;
        } finally {
            Instant end = Instant.now();
            Duration costTime = Duration.between(start, end);
            userOperLog.setOperStatus(0);
            userOperLog.setCostTime((int) costTime.toMillis());
            Db.save(userOperLog);
        }
    }
}