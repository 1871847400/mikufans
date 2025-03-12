package pers.tgl.mikufans.dto.security;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 校验验证码
 */
@Data
public class CaptchaDto implements Serializable {
    @NotBlank
    private String puzzleId;
    @NotNull
    private Float value;
}
