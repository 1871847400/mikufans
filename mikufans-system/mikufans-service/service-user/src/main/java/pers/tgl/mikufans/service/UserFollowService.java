package pers.tgl.mikufans.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.consts.FollowUserSort;
import pers.tgl.mikufans.consts.UserFollowStatus;
import pers.tgl.mikufans.domain.user.UserFollow;
import pers.tgl.mikufans.params.UserFollowParams;
import pers.tgl.mikufans.vo.UserFollowVo;

import java.util.List;

public interface UserFollowService extends BaseService<UserFollow> {
    /**
     * 创建关注
     */
    UserFollowStatus createFollow(long targetId);
    /**
     * 取消关注
     */
    boolean cancelFollow(long targetId);
    /**
     * 是否关注了
     */
    boolean isFollow(long targetId);
    /**
     * 获取关注状态
     */
    UserFollowStatus getFollowStatus(Long targetId);
    /**
     * 关注信息
     */
    UserFollow getFollow(long targetId);
    /**
     * 获得关注列表的用户id
     */
    List<Long> getFollowIds(@Nullable Long userId, FollowUserSort sort, boolean asc);
    /**
     *
     */
    default List<Long> getFollowIds(@Nullable Long userId) {
        return getFollowIds(userId, FollowUserSort.FOLLOW_TIME, false);
    }
    /**
     * 获得关注列表
     */
    Page<UserFollowVo> getFollows(UserFollowParams params);
    /**
     * 获得粉丝列表 (按照关注时间排序)
     */
    IPage<UserFollowVo> getFans(@Nullable Long userId);
    /**
     * 修改访问关注用户的时间 (只对关注用户有效)
     */
    void createAccess(Long targetUserId);
}