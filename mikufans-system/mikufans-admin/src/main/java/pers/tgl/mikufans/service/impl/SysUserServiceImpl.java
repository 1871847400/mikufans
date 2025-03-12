package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.config.TokenConfig;
import pers.tgl.mikufans.consts.CustomHeaders;
import pers.tgl.mikufans.consts.TokenType;
import pers.tgl.mikufans.domain.system.SysPerm;
import pers.tgl.mikufans.domain.system.SysRole;
import pers.tgl.mikufans.domain.system.SysUser;
import pers.tgl.mikufans.mapper.SysUserMapper;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.service.SysUserService;
import pers.tgl.mikufans.util.JsonUtils;
import pers.tgl.mikufans.util.JwtUtils;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.util.ServletUtils;
import pers.tgl.mikufans.vo.SysUserVo;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends BaseServiceImpl<SysUser, SysUserMapper> implements SysUserService {
    private final AppConfig appConfig;
    @Lazy
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUserVo getVoById(Long sysUserId) {
        if (sysUserId == null) {
            sysUserId = SecurityUtils.getContextUserId(true);
        }
        SysUser sysUser = getById(sysUserId);
        if (sysUser == null) {
            return null;
        }
        SysUserVo sysUserVo = BeanUtil.toBean(sysUser, SysUserVo.class);
        sysUserVo.setPerms(new ArrayList<>());
        if (CollUtil.isNotEmpty(sysUser.getRoleIds())) {
            List<SysRole> roles = Db.lambdaQuery(SysRole.class)
                    .select(SysRole::getRoleKey, SysRole::getPermIds)
                    .in(SysRole::getId, sysUser.getRoleIds())
                    .list();
            sysUserVo.setRoles(map(roles, SysRole::getRoleKey));
            for (SysRole role : roles) {
                if (CollUtil.isNotEmpty(role.getPermIds())) {
                    List<SysPerm> perms = Db.lambdaQuery(SysPerm.class)
                            .select(SysPerm::getPermKey)
                            .in(SysPerm::getId, role.getPermIds())
                            .list();
                    sysUserVo.getPerms().addAll(map(perms, SysPerm::getPermKey));
                }
            }
        } else {
            sysUserVo.setRoles(Collections.emptyList());
        }
        //最高管理员拥有所有权限
        if (sysUserVo.getId() == 1) {
            List<SysPerm> perms = Db.lambdaQuery(SysPerm.class)
                    .select(SysPerm::getPermKey)
                    .list();
            sysUserVo.setPerms(map(perms, SysPerm::getPermKey));
        }
        return sysUserVo;
    }

    @Override
    public UserToken login(String username, String password) {
        SysUser sysUser = getOneBy(SysUser::getUsername, username);
        if (sysUser == null || !passwordEncoder.matches(password, sysUser.getPassword())) {
            throw new BadCredentialsException("");
        }
        if (sysUser.getDisabled() == 1) {
            throw new DisabledException("");
        }
        UserToken userToken = new UserToken(sysUser.getId(), sysUser.getUsername(), sysUser.getPassword().hashCode(), 1);
        String content = JsonUtils.writeString(userToken);
        TokenConfig tokenConfig = appConfig.getToken().get(TokenType.ADMIN);
        String accessToken = JwtUtils.createJwt(content, tokenConfig.getDuration(), tokenConfig.getSecret());
        ServletUtils.getResponse().setHeader(CustomHeaders.ADMIN_TOKEN, accessToken);
        return userToken;
    }

    @Override
    public List<SysRole> getUserRoles(Long sysUserId) {
        List<Long> roleIds = getColumnValue(sysUserId, SysUser::getRoleIds);
        return CollUtil.isEmpty(roleIds) ? Collections.emptyList() : Db.listByIds(roleIds, SysRole.class);
    }

    @Override
    public List<SysPerm> getUserPerms(Long sysUserId) {
        if (sysUserId == 1) {
            return Db.query(SysPerm.class).list();
        }
        List<SysPerm> result = new ArrayList<>();
        List<Long> roleIds = getColumnValue(sysUserId, SysUser::getRoleIds);
        if (CollUtil.isEmpty(roleIds)) {
            return result;
        }
        List<SysRole> roles = Db.lambdaQuery(SysRole.class)
                .select(SysRole::getPermIds)
                .in(SysRole::getId, roleIds)
                .list();
        for (SysRole role : roles) {
            if (CollUtil.isNotEmpty(role.getPermIds())) {
                List<SysPerm> perms = Db.lambdaQuery(SysPerm.class)
                        .in(SysPerm::getId, role.getPermIds())
                        .list();
                result.addAll(perms);
            }
        }
        return result;
    }

    @Override
    public List<GrantedAuthority> getUserAuthorities(Long sysUserId) {
        List<GrantedAuthority> result = new ArrayList<>();
        for (SysRole userRole : getUserRoles(sysUserId)) {
            result.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRoleKey()));
        }
        for (SysPerm userPerm : getUserPerms(sysUserId)) {
            result.add(new SimpleGrantedAuthority(userPerm.getPermKey()));
        }
        return result;
    }
}