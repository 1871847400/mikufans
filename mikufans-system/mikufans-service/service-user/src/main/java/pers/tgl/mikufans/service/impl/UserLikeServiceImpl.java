package pers.tgl.mikufans.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.user.UserLike;
import pers.tgl.mikufans.domain.user.UserLikeData;
import pers.tgl.mikufans.event.UserLikeEvent;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.UserLikeMapper;
import pers.tgl.mikufans.service.UserLikeDataService;
import pers.tgl.mikufans.service.UserLikeService;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;

import javax.annotation.Resource;

@Service
@RequiredArgsConstructor
public class UserLikeServiceImpl extends BaseServiceImpl<UserLike, UserLikeMapper> implements UserLikeService {
    @Resource
    @Lazy
    private UserLikeDataService userLikeDataService;

    @Override
    public int getLikeVal(Long likeDataId) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (contextUserId == null) {
            return 0;
        }
        UserLike userLike = wrapper()
                .select(UserLike::getLikeVal)
                .eq(UserLike::getLikeDataId, likeDataId)
                .eq(UserLike::getUserId, contextUserId)
                .one();
        if (userLike == null) {
            return 0;
        }
        return userLike.getLikeVal();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLike(Long likeDataId, int likeVal) {
        if (likeVal != 0 && likeVal != UserLike.LIKE_VAL && likeVal != UserLike.DISLIKE_VAL) {
            throw new CustomException("参数错误");
        }
        UserLikeData likeData = userLikeDataService.getById(likeDataId);
        if (likeData == null) {
            throw new CustomException("无法点赞");
        }
        Long contextUserId = SecurityUtils.getContextUserId(true);
        UserLike oldLike = wrapper().eq(UserLike::getLikeDataId, likeDataId)
                .eq(UserLike::getUserId, contextUserId)
                .one();
        if (oldLike != null && oldLike.getLikeVal() == likeVal) {
            return;
        }
        removeById(oldLike);
        UserLike newLike = new UserLike();
        newLike.setLikeDataId(likeDataId);
        newLike.setLikeVal(likeVal);
        newLike.setUserId(contextUserId);
        //如果既非点赞又非点踩则跳过保存
        if (likeVal > 0) {
            save(newLike);
        }
        publishEvent(new UserLikeEvent(this, likeData, newLike, oldLike));
    }

    @Override
    public PageImpl<UserLike> getUserLikes(Long likeDataId) {
        return wrapper()
                .eq(UserLike::getLikeDataId, likeDataId)
                .page(BaseController.createPage());
    }
}