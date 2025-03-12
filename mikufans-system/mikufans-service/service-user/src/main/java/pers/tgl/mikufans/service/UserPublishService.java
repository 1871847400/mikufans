package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.user.UserPublish;
import pers.tgl.mikufans.dto.UserPublishDto;

public interface UserPublishService extends BaseService<UserPublish> {
    /**
     * 创建说说
     */
    UserPublish createDto(UserPublishDto dto);
    /**
     * 更新说说
     */
    void updateDto(UserPublishDto dto);
}