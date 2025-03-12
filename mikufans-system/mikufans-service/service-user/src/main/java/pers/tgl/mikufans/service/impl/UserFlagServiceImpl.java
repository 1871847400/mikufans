package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.enums.UserFlags;
import pers.tgl.mikufans.domain.user.UserFlag;
import pers.tgl.mikufans.dto.UserFlagDto;
import pers.tgl.mikufans.mapper.UserFlagMapper;
import pers.tgl.mikufans.service.UserFlagService;
import pers.tgl.mikufans.util.SecurityUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UserFlagServiceImpl extends BaseServiceImpl<UserFlag, UserFlagMapper> implements UserFlagService {
    @Override
    public Map<UserFlags, String> getUserFlags(@Nullable Long userId) {
        if (userId == null) {
            userId = SecurityUtils.getContextUserId(true);
        }
        Map<UserFlags, String> result = new LinkedHashMap<>();
        List<UserFlag> flags = lambdaQuery()
                .eq(UserFlag::getUserId, userId)
                .list();
        for (UserFlag flag : flags) {
            result.put(flag.getFlagKey(), flag.getFlagValue());
        }
        //补充默认值
        for (UserFlags userFlags : UserFlags.values()) {
            if (!result.containsKey(userFlags)) {
                result.put(userFlags, userFlags.getDefValue().toString());
            }
        }
        return result;
    }

    @Override
    public void saveUserFlag(UserFlagDto dto) {
        Long userId = SecurityUtils.getContextUserId(true);
        UserFlag userFlag = lambdaQuery()
                .eq(UserFlag::getFlagKey, dto.getFlagKey())
                .eq(UserFlag::getUserId, userId)
                .one();
        if(userFlag == null) {
            userFlag = BeanUtil.toBean(dto, UserFlag.class);
            save(userFlag);
        } else {
            UserFlag update = new UserFlag();
            update.setId(userFlag.getId());
            update.setFlagValue(dto.getFlagValue());
            updateById(update);
        }
    }

    @Override
    public boolean isDeny(Long userId, UserFlags flag) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (userId == null || Objects.equals(contextUserId, userId)) {
            return false;
        }
        UserFlag userFlag = lambdaQuery()
                .select(UserFlag::getFlagValue)
                .eq(UserFlag::getUserId, userId)
                .eq(UserFlag::getFlagKey, flag)
                .one();
        if (userFlag == null) {
            return "false".equals(flag.getDefValue().toString());
        }
        return "false".equals(userFlag.getFlagValue());
    }
}