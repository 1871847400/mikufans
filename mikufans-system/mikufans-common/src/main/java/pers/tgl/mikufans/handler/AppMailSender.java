package pers.tgl.mikufans.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

@Slf4j
@Component
public class AppMailSender {
    @Resource
    private ThreadPoolTaskExecutor executor;

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailSenderName;

    /**
     * 异步发送邮件
     * @param subject 主题
     * @param html 正文
     * @param receiver 接收人
     */
    public void sendAsync(String subject, String html, String receiver, @Nullable Runnable onFail) {
        executor.submit(() -> {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            try {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                mimeMessageHelper.setFrom(mailSenderName); //必须再指定一次发件人
                mimeMessageHelper.setTo(receiver);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(html, true);
                mailSender.send(mimeMessage);
                log.info("发送邮件成功, 收件人: {}", receiver);
            } catch (Exception e) {
                log.warn("邮件发送失败, 收件人: {} 原因: {}", receiver, e.getMessage());
                if (onFail != null) {
                    onFail.run();
                }
            }
        });
    }
}