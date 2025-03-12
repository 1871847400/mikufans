package pers.tgl.mikufans.service;

import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.enums.UserFlags;
import pers.tgl.mikufans.domain.user.UserFlag;
import pers.tgl.mikufans.dto.UserFlagDto;

import java.util.Map;

public interface UserFlagService extends BaseService<UserFlag> {
    /**
     * 获取用户所有标记,如果未设置会补充默认值
     */
    Map<UserFlags, String> getUserFlags(@Nullable Long userId);
    /**
     * 修改标记
     */
    void saveUserFlag(UserFlagDto dto);
    /**
     * 指定用户是否禁止该标记，如果未设置返回标记默认值
     */
    boolean isDeny(Long userId, UserFlags flag);
}