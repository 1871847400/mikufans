package pers.tgl.mikufans.dto.security;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmailLoginDto extends EmailValidateDto {
    @Length(min = 2, max = 16, message = "昵称长度2-16位")
    private String nickname;

    @Length(min = 6, max = 24, message = "密码长度6-24位")
    private String password;
}