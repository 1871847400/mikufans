package pers.tgl.mikufans.consts;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pers.tgl.mikufans.domain.base.BaseEntity;
import pers.tgl.mikufans.domain.user.UserFollow;

@RequiredArgsConstructor
@Getter
public enum FollowUserSort {
    /**
     * 按照访问次数排序
     */
    ACCESS("t", UserFollow::getAccessCount),
    /**
     * 按照关注时间排序
     */
    FOLLOW_TIME("t", UserFollow::getCreateTime),
    /**
     * 按照最新动态时间排序
     */
    LAST_DYNAMIC("t1", BaseEntity::getCreateTime);

    private final String alias;
    private final SFunction<UserFollow, ?> column;
}