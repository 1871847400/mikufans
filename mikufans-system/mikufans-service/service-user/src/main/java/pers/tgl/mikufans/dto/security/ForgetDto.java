package pers.tgl.mikufans.dto.security;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ForgetDto implements Serializable {
    @NotBlank
    private String uuid;

    @NotBlank
    @Length(min = 6, max = 24, message = "密码长度6-24位")
    private String password;
}
