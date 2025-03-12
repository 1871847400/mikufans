package pers.tgl.mikufans.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.exception.TokenAuthenticationException;
import pers.tgl.mikufans.util.MyUtils;
import pers.tgl.mikufans.util.R;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MySecurityExceptionHandler implements AccessDeniedHandler, AuthenticationEntryPoint {
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        accessDeniedException.printStackTrace();
    }

    /**
     * 处理认证期间的异常 (默认为统一返回403)
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        R<?> error = R.error(Code.NOT_LOGIN);
        if (authException instanceof TokenAuthenticationException) {
            Exception rawException = ((TokenAuthenticationException) authException).getRawException();
            if (rawException instanceof ExpiredJwtException) {
                error = R.error(Code.TOKEN_EXPIRED);
            } else if (rawException instanceof DisabledException) {
                error = R.error(Code.AUTH_DISABLED);
            } else {
                error.setMsg(authException.getMessage());
            }
        }
        MyUtils.writeJSONString(response, objectMapper.writeValueAsString(error));
    }
}