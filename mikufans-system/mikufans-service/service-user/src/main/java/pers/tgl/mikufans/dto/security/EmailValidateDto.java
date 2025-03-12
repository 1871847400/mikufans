package pers.tgl.mikufans.dto.security;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 邮箱验证dto
 */
@Data
public class EmailValidateDto implements Serializable {
    /**
     * 唯一id
     */
    @NotBlank(message = "请先发送验证码")
    private String uuid;
    /**
     * 邮箱
     */
    @NotBlank(message = "请输入邮箱地址")
    private String email;
    /**
     * 用户输入的验证码
     */
    @NotBlank(message = "请输入验证码")
    private String code;
}