package pers.tgl.mikufans.service;

import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import pers.tgl.mikufans.domain.system.SysPerm;
import pers.tgl.mikufans.domain.system.SysRole;
import pers.tgl.mikufans.domain.system.SysUser;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.vo.SysUserVo;

import java.util.List;

public interface SysUserService extends BaseService<SysUser> {
    /**
     *
     */
    SysUserVo getVoById(@Nullable Long sysUserId);
    /**
     * 登录
     */
    UserToken login(String username, String password);
    /**
     * 获取系统用户所有角色
     */
    List<SysRole> getUserRoles(Long sysUserId);
    /**
     * 获取系统用户所有权限
     */
    List<SysPerm> getUserPerms(Long sysUserId);

    /**
     * 获取兼容spring security的权限列表
     * 包含权限和角色，角色以ROLE_开头
     */
    List<GrantedAuthority> getUserAuthorities(Long sysUserId);
}