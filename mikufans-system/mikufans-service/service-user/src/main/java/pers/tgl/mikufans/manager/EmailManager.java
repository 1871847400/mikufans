package pers.tgl.mikufans.manager;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pers.tgl.mikufans.dto.security.EmailValidateDto;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.handler.AppMailSender;
import pers.tgl.mikufans.util.IpUtils;
import pers.tgl.mikufans.util.MyUtils;
import pers.tgl.mikufans.util.RedisUtils;
import pers.tgl.mikufans.util.ServletUtils;
import pers.tgl.mikufans.vo.EmailSendResult;

import java.io.Serializable;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailManager implements Serializable {
    private final RedisUtils redisUtils;
    private final TemplateEngine templateEngine;
    private final AppMailSender appMailSender;

    public EmailSendResult sendEmail(final String email, String action) {
        if (StrUtil.isBlank(email)) {
            throw new CustomException("邮箱格式不正确");
        }
        String emailTimeKey = action + "_time:" + IpUtils.getIpaddr(ServletUtils.getRequest()) + ":" + email;
        if (redisUtils.exists(emailTimeKey)) {
            Long seconds = redisUtils.ttl(emailTimeKey, TimeUnit.SECONDS);
            throw new CustomException("邮件验证冷却中,剩余"+seconds+"秒");
        }
        final String uuid = UUID.fastUUID().toString(true);
        final String redisKey = action + ":" + uuid;
        final long now = System.currentTimeMillis();
        // 生成数字验证码
        String code = MyUtils.generateNumbers(4);
        Duration timeout = Duration.ofMinutes(15);
        Map<String, Object> map = new LinkedHashMap<>(4);
        map.put("email", email);
        map.put("code", code);
        map.put("time", now);
        map.put("fail", 0);
        redisUtils.hset(redisKey, map);
        //邮箱验证有效时间
        redisUtils.expire(redisKey, timeout);
        //设置发送邮件冷却时间
        redisUtils.set(emailTimeKey, now, Duration.ofMinutes(1));
        // 设置thymeleaf模板环境变量
        Context context = new Context();
        context.setVariable("username", email);
        context.setVariable("timeout", timeout.toMinutes());
        context.setVariable("code", code);
        // 加载邮件模板
        String html = templateEngine.process("email_verify", context);
        log.info("发送验证邮件 收件人{} 密码{}", email, code);
        //异步发送邮件
        appMailSender.sendAsync("邮箱验证", html, email, null);
        //将验证信息返回
        EmailSendResult emailSendResult = new EmailSendResult();
        emailSendResult.setUuid(uuid);
        emailSendResult.setTimeout((int) timeout.getSeconds());
        emailSendResult.setInterval(60);
        return emailSendResult;
    }

    public void verifyEmail(String action, EmailValidateDto dto) {
        String redisKey = action + ":" + dto.getUuid();
        if (!redisUtils.exists(redisKey)) {
            throw new CustomException("请先发送验证码");
        }
        String email = redisUtils.hget(redisKey, "email");
        if (!Objects.equals(email, dto.getEmail())) {
            throw new CustomException("邮箱不一致");
        }
        String code = redisUtils.hget(redisKey, "code");
        if (!Objects.equals(dto.getCode(), code)) {
            long fail = redisUtils.increment(redisKey, "fail", 1);
            if (fail >= 5) {
                redisUtils.del(redisKey);
                throw new CustomException("验证码错误，尝试次数已用光！");
            } else {
                throw new CustomException("验证码错误，剩余尝试次数: " + (5 - fail));
            }
        }
        redisUtils.del(redisKey);
    }
}