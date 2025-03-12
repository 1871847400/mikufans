package pers.tgl.mikufans.domain.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.enums.UserFlags;

/**
 * 用户设置表
 * @TableName user_flag
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserFlag extends UserBaseEntity {
    /**
     * 标志键值
     */
    private UserFlags flagKey;
    /**
     * 标志的值
     */
    private String flagValue;
}