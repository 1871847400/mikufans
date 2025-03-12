package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.system.SysUser;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserVo extends SysUser {
    /**
     * 权限标识列表
     */
    private List<String> perms;
    /**
     * 角色标识列表
     */
    private List<String> roles;
}