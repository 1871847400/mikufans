package pers.tgl.mikufans.controller.security;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.aop.login.LoginLimit;
import pers.tgl.mikufans.aop.repeat.RepeatSubmit;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.config.TokenConfig;
import pers.tgl.mikufans.consts.CustomHeaders;
import pers.tgl.mikufans.consts.TokenType;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.controller.oauth.OAuthApi;
import pers.tgl.mikufans.controller.oauth.OAuthUser;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.domain.user.UserOperLog;
import pers.tgl.mikufans.dto.security.EmailLoginDto;
import pers.tgl.mikufans.dto.security.EmailSendDto;
import pers.tgl.mikufans.dto.security.LoginDto;
import pers.tgl.mikufans.dto.security.OAuthLoginDto;
import pers.tgl.mikufans.event.UserTokenEvent;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.manager.EmailManager;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.service.UserService;
import pers.tgl.mikufans.util.JsonUtils;
import pers.tgl.mikufans.util.JwtUtils;
import pers.tgl.mikufans.util.MyUtils;
import pers.tgl.mikufans.vo.EmailSendResult;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController extends BaseController {
    private final UserService userService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AppConfig appConfig;
    private final EmailManager emailManager;
    private final CaptchaController captchaController;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 用户登录
     * 如果登录失败了,短时间再次登录需要验证码
     * 登录失败次数超过限额后会暂时禁止登录
     * 登录成功后清除失败记录
     */
    @PostMapping("/login")
    @LoginLimit(value = "#dto.username")
    @AppLog(value = "密码登录", type = UserOperLog.OPER_TYPE_LOGIN)
    public void login(@RequestBody @Validated LoginDto dto) {
        //校验 验证码的有效性
        captchaController.validated(dto.getPuzzleId());
        //如果不是用邮箱登录则判断为用昵称登录
        if (!MyUtils.isValidEmail(dto.getUsername())) {
            String username = userService.getUsernameByNick(dto.getUsername());
            dto.setUsername(username);
        }
        this.userLogin(dto.getUsername(), dto.getPassword());
    }

    /**
     * 发送邮件
     */
    @PostMapping("/email/send")
    public EmailSendResult requestEmail(@RequestBody @Validated EmailSendDto dto) {
        captchaController.validated(dto.getPuzzleId());
        return emailManager.sendEmail(dto.getEmail(), "login");
    }

    /**
     * 使用邮箱验证 登录或注册
     */
    @AppLog(value = "邮箱登录", type = UserOperLog.OPER_TYPE_LOGIN)
    @RepeatSubmit(interval = 1000)
    @PostMapping("/email/login")
    public void login(@RequestBody @Validated EmailLoginDto dto) {
        emailManager.verifyEmail("login", dto);
        User user = userService.getOneBy(User::getUsername, dto.getEmail());
        //如果用户不存在代表注册,自动创建用户
        if (user == null) {
            String nickname = dto.getNickname();
            String password = dto.getPassword();
            if (StrUtil.isBlank(nickname)) {
                //设置默认昵称
                nickname = "Miku_" + RandomUtil.randomString(8);
            }
            if (StrUtil.isBlank(password)) {
                //设置随机密码
                password = RandomUtil.randomString(24);
            }
            user = userService.createUser(dto.getEmail(), nickname, password);
        }
        UserToken userToken = new UserToken(user.getId(), user.getUsername(), user.getPassword().hashCode(), 0);
        setToken(userToken);
    }

    /**
     * 使用 oauth2.0 进行登录
     */
    @AppLog(value = "第三方登录", type = UserOperLog.OPER_TYPE_LOGIN)
    @RepeatSubmit(interval = 1000)
    @PostMapping("/oauth/login")
    public void oauthLogin(@RequestBody @Validated OAuthLoginDto dto) {
        Map<String, OAuthApi> apis = SpringUtil.getBeansOfType(OAuthApi.class);
        OAuthApi oAuthApi = null;
        for (OAuthApi api : apis.values()) {
            if (Objects.equals(dto.getOauthType(), api.getAuthType())) {
                oAuthApi = api;
                break;
            }
        }
        if (oAuthApi == null) {
            throw new CustomException("不支持的登录方式");
        }
        OAuthUser oAuthUser;
        try {
            String accessToken = oAuthApi.getAccessToken(dto.getOauthCode());
            oAuthUser = oAuthApi.getOAuthUser(accessToken);
        } catch (Exception e) {
            throw new CustomException("请求第三方登录失败！");
        }
        String email = oAuthUser.getEmail();
        //强制要求邮箱存在
        if (StrUtil.isBlank(email)) {
            throw new CustomException("未设置邮箱,无法完成第三方登录");
        }
        User user = userService.getOneBy(User::getUsername, email);
        //用户不存在,自动注册
        if (user == null) {
            String nickname = oAuthUser.getNickname();
            if (StrUtil.isBlank(nickname)) {
                nickname = "Miku_" + RandomUtil.randomString(8);
            }
            String password = RandomUtil.randomString(24);
            user = userService.createUser(email, nickname, password);
        }
        UserToken userToken = new UserToken(user.getId(), user.getUsername(), user.getPassword().hashCode(), 0);
        setToken(userToken);
    }

    @GetMapping("/oauth/list")
    public Map<String, String> listAuth() {
        Map<String, String> result = new LinkedHashMap<>();
        Map<String, OAuthApi> apis = SpringUtil.getBeansOfType(OAuthApi.class);
        for (Map.Entry<String, OAuthApi> e : apis.entrySet()) {
            result.put(e.getValue().getAuthType(), e.getValue().getAuthCodeUrl());
        }
        return result;
    }

    public void userLogin(String username, String password) {
        AuthenticationManager authenticationManager = authenticationManagerBuilder.getOrBuild();
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        //验证账号和密码
        //authenticate!=传参的authentication 是基于loadUserByUsername的结果
        Authentication authenticate = authenticationManager.authenticate(authentication);
        //如果验证通过则能获取loadUserByUsername返回的UserDetails
        User user = (User) authenticate.getPrincipal();
        UserToken userToken = new UserToken(user.getId(), user.getUsername(), user.getPassword().hashCode(), 0);
        setToken(userToken);
    }

    private void setToken(UserToken userToken) {
        String content = JsonUtils.writeString(userToken);
        //生成访问token,短时间有效
        TokenConfig accessTokenConfig = appConfig.getToken().get(TokenType.ACCESS);
        String accessToken = JwtUtils.createJwt(content, accessTokenConfig.getDuration(), accessTokenConfig.getSecret());
        getResponse().setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        //生成刷新token,长期有效
        TokenConfig refreshTokenConfig = appConfig.getToken().get(TokenType.REFRESH);
        String refreshToken = JwtUtils.createJwt(content, refreshTokenConfig.getDuration(), refreshTokenConfig.getSecret());
        getResponse().setHeader(CustomHeaders.REFRESH_TOKEN, refreshToken);
        //发布事件
        eventPublisher.publishEvent(new UserTokenEvent(this, userToken));
    }

    /**
     * 使用 refreshToken 刷新 双token
     * 双token能够增强对用户的控制力
     * 返回 true 刷新成功
     * 返回 false 刷新失败
     */
    @AppLog("刷新TOKEN")
    @PostMapping("/refresh-token")
    public void refreshToken() {
        String refreshToken = getRequest().getHeader(CustomHeaders.REFRESH_TOKEN);
        if (StrUtil.isBlank(refreshToken)) {
            throw new CustomException("token is empty");
        }
        TokenConfig refreshTokenConfig = appConfig.getToken().get(TokenType.REFRESH);
        UserToken userToken;
        try {
            Claims claims = JwtUtils.parseJwt(refreshToken, refreshTokenConfig.getSecret());
            userToken = JsonUtils.read(claims.getSubject(), UserToken.class);
        } catch (JwtException e) {
            throw new CustomException("TOKEN已失效");
        }
        if (userToken == null) {
            throw new CustomException("TOKEN无效");
        }
        User user = userService.getById(userToken.getId());
        if (user == null || user.getDisabled() == 1) {
            throw new CustomException("用户不存在或已被禁用");
        }
        int version = user.getPassword().hashCode();
        if (!Objects.equals(userToken.getVersion(), version)) {
            throw new CustomException("认证信息已失效,请重新登录！");
        }
        setToken(new UserToken(user.getId(), user.getUsername(), version, 0));
    }
}