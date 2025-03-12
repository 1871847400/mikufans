package pers.tgl.mikufans.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * 验证token时所有异常统一为此
 */
@Getter
public class TokenAuthenticationException extends AuthenticationException {
    //原始异常
    //本来抛出的异常是什么?比如到期或无法解析
    private Exception rawException;

    public TokenAuthenticationException(String msg) {
        super(msg);
    }

    public TokenAuthenticationException(String msg, Exception rawException) {
        super(msg);
        this.rawException = rawException;
    }
}