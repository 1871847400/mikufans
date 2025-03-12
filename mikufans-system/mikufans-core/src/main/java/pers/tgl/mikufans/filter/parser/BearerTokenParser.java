package pers.tgl.mikufans.filter.parser;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.consts.TokenType;
import pers.tgl.mikufans.exception.TokenAuthenticationException;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.util.JwtUtils;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class BearerTokenParser implements TokenParser {
    private final AppConfig appConfig;
    private final ObjectMapper objectMapper;

    @Override
    public String getAuthScheme() {
        return "Bearer";
    }

    @Override
    public UserToken parse(HttpServletRequest request, String credentials) {
        boolean isAdmin = request.getRequestURI().startsWith("/admin");
        String secret = appConfig.getToken().get(isAdmin ? TokenType.ADMIN : TokenType.ACCESS).getSecret();
        Claims claims = JwtUtils.parseJwt(credentials, secret);
        try {
            return objectMapper.readValue(claims.getSubject(), UserToken.class);
        } catch (JacksonException e) {
            throw new TokenAuthenticationException("token格式不正确", e);
        }
    }
}