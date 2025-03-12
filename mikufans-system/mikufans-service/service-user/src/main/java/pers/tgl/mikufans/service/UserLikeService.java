package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.user.UserLike;
import pers.tgl.mikufans.util.PageImpl;

public interface UserLikeService extends BaseService<UserLike> {
    /**
     * 获取当前用户对目标id的点赞值
     */
    int getLikeVal(Long likeDataId);
    /**
     * 创建或删除点赞或点踩
     */
    void saveLike(Long likeDataId, int likeVal);
    /**
     * 获取点赞的列表
     */
    PageImpl<UserLike> getUserLikes(Long likeDataId);
}