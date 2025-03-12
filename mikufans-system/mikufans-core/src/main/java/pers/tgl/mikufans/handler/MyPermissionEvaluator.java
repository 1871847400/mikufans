package pers.tgl.mikufans.handler;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.model.UserToken;

import java.io.Serializable;
import java.util.Collection;

/**
 * 自定义实体权限控制器
 * 相比 hasAuthority 针对对象的权限
 * 匿名用户默认拥有权限：ROLE_ANONYMOUS
 */
@Component
public class MyPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication.getDetails() instanceof UserToken) {
            UserToken userToken = (UserToken) authentication.getDetails();
            if (userToken.getId() == 1 && userToken.getUserType() == 1) {
                return true;
            }
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(targetDomainObject.toString());
        if (tableInfo == null) {
            return false;
        }
        PermFlag permFlag = tableInfo.getEntityType().getAnnotation(PermFlag.class);
        if (permFlag == null) {
            return false;
        }
        String prefix = permFlag.group().getPermPrefix();
        String requirePermission = prefix + ":" + targetDomainObject + ":" + permission.toString();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (StrUtil.equalsIgnoreCase(requirePermission, authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPermission(authentication, targetType, permission);
    }
}