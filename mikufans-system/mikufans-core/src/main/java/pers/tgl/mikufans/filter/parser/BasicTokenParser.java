package pers.tgl.mikufans.filter.parser;

import cn.hutool.core.codec.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.exception.TokenAuthenticationException;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.service.SysUserService;

import javax.servlet.http.HttpServletRequest;

/**
 * 允许访问部分后台地址时直接带上用户名和密码
 * 例如：curl -X POST -u root:123456 {ip}/admin/actuator/shutdown
 * 登录信息会变成: Authorization: Basic cm9vdDoxMjM0NTY=
 */
@Component
@RequiredArgsConstructor
public class BasicTokenParser implements TokenParser {
    private final SysUserService sysUserService;

    @Override
    public String getAuthScheme() {
        return "Basic";
    }

    @Override
    public UserToken parse(HttpServletRequest request, String credentials) {
        if (!Base64.isBase64(credentials)) {
            throw new TokenAuthenticationException("验证信息格式不正确");
        }
        String authInfo = Base64.decodeStr(credentials);
        String username = authInfo.split(":")[0];
        String password = authInfo.split(":")[1];
        return sysUserService.login(username, password);
    }
}