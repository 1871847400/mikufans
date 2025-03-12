package pers.tgl.mikufans.domain.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.enums.UserFavorType;

@EqualsAndHashCode(callSuper = true)
@TableName(value ="user_favor")
@Data
@PermFlag(name = "用户爱好", group = PermGroup.BUSINESS)
public class UserFavor extends UserBaseEntity {
    private UserFavorType favorType;

    private String tag;
}