package pers.tgl.mikufans.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class SysUserLoginDto implements Serializable {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String uuid;
    @NotBlank
    private String captcha;
}