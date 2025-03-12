package pers.tgl.mikufans.dto.security;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.dto.UserDto;

import javax.validation.Valid;

/**
 * 邮箱登录/注册dto
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EmailLoginDto extends EmailValidateDto {
    /**
     * 注册时完成个人信息填写
     * 用户注册时才可以携带(非必须)
     */
    @Valid
    private UserDto user;
}