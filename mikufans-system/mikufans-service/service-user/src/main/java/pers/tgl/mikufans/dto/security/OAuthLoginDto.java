package pers.tgl.mikufans.dto.security;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class OAuthLoginDto implements Serializable {
    @NotBlank(message = "必须指定登录方式")
    private String oauthType;
    @NotBlank(message = "必须携带授权码")
    private String oauthCode;
}