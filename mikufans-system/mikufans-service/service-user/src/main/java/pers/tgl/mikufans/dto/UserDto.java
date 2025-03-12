package pers.tgl.mikufans.dto;

import lombok.Data;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.validator.ExtendValidation;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 用户信息
 */
@Data
@ExtendValidation(User.class)
public class UserDto implements Serializable {
    private Long id; //继承校验会用到
    private String nickname;
    private String password;
    private Integer gender;
    private LocalDate birthday;
    private String sign;
    private Long avatarId;
    private String background;
}