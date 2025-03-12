package pers.tgl.mikufans.controller.security;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.dto.security.EmailSendDto;
import pers.tgl.mikufans.dto.security.EmailValidateDto;
import pers.tgl.mikufans.dto.security.ForgetDto;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.manager.EmailManager;
import pers.tgl.mikufans.service.UserService;
import pers.tgl.mikufans.util.RedisUtils;
import pers.tgl.mikufans.vo.EmailSendResult;

import java.time.Duration;

@RestController
@Validated
@RequestMapping("/auth/forget")
@RequiredArgsConstructor
public class ForgetController extends BaseController {
    private final UserService userService;
    private final RedisUtils redisUtils;
    private final EmailManager emailManager;
    private final CaptchaController captchaController;

    /**
     * 第一步：发送邮件
     */
    @AppLog("忘记密码-发送邮件")
    @PostMapping("/email/send")
    public EmailSendResult sendEmail(@RequestBody @Validated EmailSendDto dto) {
        captchaController.validated(dto.getPuzzleId());
        if (!userService.existsBy(User::getUsername, dto.getEmail())) {
            throw new CustomException("不存在的邮箱账号！");
        }
        return emailManager.sendEmail(dto.getEmail(), "forget");
    }
    /**
     * 第二步：验证邮件
     */
    @AppLog("忘记密码-验证邮件")
    @PostMapping("/email/verify")
    public void verifyEmail(@RequestBody @Validated EmailValidateDto dto) {
        emailManager.verifyEmail("forget", dto);
        String redisKey = "pwd:" + dto.getUuid();
        redisUtils.set(redisKey, dto.getEmail(), Duration.ofMinutes(10));
    }
    /**
     * 第三步：修改密码
     */
    @AppLog("忘记密码-修改密码")
    @PutMapping("/email/pwd")
    public void changePassword(@RequestBody @Validated ForgetDto dto) {
        String redisKey = "pwd:" + dto.getUuid();
        String username = redisUtils.getString(redisKey, "");
        if (StrUtil.isBlank(username)) {
            throw new CustomException("请先验证邮箱");
        }
        User user = userService.getOneBy(User::getUsername, username);
        if (user == null) {
            throw new CustomException("用户不存在");
        }
        redisUtils.del(redisKey);
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setPassword(dto.getPassword());
        Db.updateById(updateUser);
    }
}