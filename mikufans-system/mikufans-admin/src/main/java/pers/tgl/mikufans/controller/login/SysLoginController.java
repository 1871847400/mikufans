package pers.tgl.mikufans.controller.login;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.aop.login.LoginLimit;
import pers.tgl.mikufans.domain.user.UserOperLog;
import pers.tgl.mikufans.dto.SysUserLoginDto;
import pers.tgl.mikufans.service.SysUserService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class SysLoginController {
    private final SysCaptchaController sysCaptchaController;
    private final SysUserService sysUserService;

    @LoginLimit(value = "#dto.username")
    @PostMapping("/login")
    @AppLog(value = "管理员登录", type = UserOperLog.OPER_TYPE_LOGIN)
    public void login(@RequestBody @Validated SysUserLoginDto dto) {
        sysCaptchaController.verify(dto.getUuid(), dto.getCaptcha());
        sysUserService.login(dto.getUsername(), dto.getPassword());
    }
}