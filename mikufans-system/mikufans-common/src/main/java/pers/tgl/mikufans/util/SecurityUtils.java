package pers.tgl.mikufans.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.consts.Roles;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.model.UserToken;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class SecurityUtils {
    /**
     * 获取当前请求的用户
     * 如果为null代表未登录状态
     */
    @Nullable
    public static UserToken getContextUserToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            Object details = authentication.getDetails();
            if (details instanceof UserToken) {
                return (UserToken) details;
            }
        }
        return null;
    }
    /**
     * 手动设置当前上下文用户
     */
    public static void setContextUserToken(UserToken userToken) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userToken, "");
        token.setDetails(userToken);
        SecurityContextHolder.getContext().setAuthentication(token);
    }
    /**
     * 获取当前请求用户的userId
     */
    public static Long getContextUserId(boolean throwEx) {
        UserToken user = getContextUserToken();
        if (user == null && throwEx) {
            throw new AuthenticationCredentialsNotFoundException("请先登录");
        }
        return user != null ? user.getId() : null;
    }

    public static void checkUserId(Long targetUserId) throws AccessDeniedException {
        UserToken user = getContextUserToken();
        if (user == null || !Objects.equals(user.getId(), targetUserId)) {
            throw new AccessDeniedException("没有权限");
        }
    }
    /**
     * 获取当前登录用户的名称 未登录的情况下可能是：anonymousUser
     * @throws AuthenticationCredentialsNotFoundException AuthenticationCredentialsNotFound
     */
    @Deprecated
    public static String getContextUsername() throws AuthenticationCredentialsNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            throw new AuthenticationCredentialsNotFoundException("");
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException("匿名用户");
        }
        return authentication.getName();
    }
    /**
     * 获取当前请求用户的所有权限(如果未登录返回空集合)
     */
    public static Collection<? extends GrantedAuthority> getContextUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Collections.emptyList();
        }
        return authentication.getAuthorities();
    }
    public static void checkPermission(String permission) {
        if (StrUtil.isBlank(permission)) {
            return;
        }
        Collection<? extends GrantedAuthority> authorities = getContextUserAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(permission)) {
                return;
            }
        }
        throw new CustomException(Code.FORBIDDEN);
    }

    /**
     * 获得用户某权限级别，如: user.xxxx.count.10 => 10
     */
    public static int getContextUserPower(String permission, int defaultValue) {
        if (!permission.endsWith(".")) {
            permission += ".";
        }
        for (GrantedAuthority ga : getContextUserAuthorities()) {
            if (ga.getAuthority().startsWith(permission)) {
                try {
                    return Integer.parseInt(ga.getAuthority().replace(permission, ""));
                } catch (NumberFormatException e) {
                    break;
                }
            }
        }
        return defaultValue;
    }

    public static boolean isAdminUser() {
        UserToken userToken = getContextUserToken();
        if (userToken == null) {
            return false;
        }
        return userToken.getUserType() == 1 || hasAnyRole(Roles.ADMIN);
    }

    /**
     * @param roleNames 当前用户是否拥有任意一个角色
     */
    public static boolean hasAnyRole(String... roleNames) {
        if (roleNames == null || roleNames.length == 0) {
            return true;
        }
        for (String roleName : roleNames) {
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }
            Collection<? extends GrantedAuthority> authorities = getContextUserAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(roleName)) {
                    return true;
                }
            }
        }
        return false;
    }
}