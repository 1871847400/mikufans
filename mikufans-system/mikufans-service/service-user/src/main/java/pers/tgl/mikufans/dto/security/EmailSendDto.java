package pers.tgl.mikufans.dto.security;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 请求发送邮箱验证码
 */
@Data
public class EmailSendDto implements Serializable {
    @Email(message = "邮箱格式不正确")
    @NotBlank
    private String email;

    @NotBlank
    private String puzzleId;
}