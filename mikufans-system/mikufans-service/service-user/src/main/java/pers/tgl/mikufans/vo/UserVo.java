package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pers.tgl.mikufans.consts.UserFollowStatus;
import pers.tgl.mikufans.domain.enums.UserFlags;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.domain.user.UserDynamic;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class UserVo extends User {
    /**
     * 当前用户关注该名用户的信息
     */
    private UserFollowStatus follow;
    /**
     * 最新动态
     */
    private UserDynamic lastDynamic;
    /**
     * 升到下一级需要的总经验值
     */
    private Integer nextExp;
    /**
     * 高亮后的昵称
     */
    private String highlightNickname;
    /**
     * 标记
     */
    private Map<UserFlags, String> flags;
}