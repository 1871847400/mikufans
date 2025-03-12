package pers.tgl.mikufans.domain.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
public class UserFollow extends UserBaseEntity {
    /**
     * 关注的目标用户id
     */
    private Long targetId;
    /**
     * 访问次数
     */
    private Integer accessCount;
    /**
     * 上次访问时间
     */
    private Date accessTime;
}