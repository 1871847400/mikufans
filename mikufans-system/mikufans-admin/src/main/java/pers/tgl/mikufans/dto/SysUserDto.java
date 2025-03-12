package pers.tgl.mikufans.dto;

import lombok.Data;
import pers.tgl.mikufans.domain.system.SysUser;
import pers.tgl.mikufans.validator.ExtendValidation;

import java.io.Serializable;

@Data
@ExtendValidation(SysUser.class)
public class SysUserDto implements Serializable {
    private String username;
    private String password;
}