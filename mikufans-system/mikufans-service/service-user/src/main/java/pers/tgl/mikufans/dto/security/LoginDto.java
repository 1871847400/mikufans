package pers.tgl.mikufans.dto.security;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录
 */
@Data
public class LoginDto implements Serializable {
    /**
     * 可能是昵称也可能是邮箱
     */
    @NotBlank
    private String username;
    /**
     * 登录密码
     */
    @NotBlank
    private String password;
    /**
     * 登录时的验证码id
     */
    @NotBlank
    private String puzzleId;
}