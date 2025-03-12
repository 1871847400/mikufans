package pers.tgl.mikufans.service.impl;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.consts.Period;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoCoin;
import pers.tgl.mikufans.event.PayCoinEvent;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.VideoCoinMapper;
import pers.tgl.mikufans.service.VideoCoinService;
import pers.tgl.mikufans.util.DbUtils;
import pers.tgl.mikufans.util.SecurityUtils;

/**
 * 注意高并发线程安全问题,获取Count时可能是脏数据
 */
@Service
public class VideoCoinServiceImpl extends BaseServiceImpl<VideoCoin, VideoCoinMapper>
        implements VideoCoinService {
    /**
     * 最多可以给每个视频投多少个币
     */
    private static final int VIDEO_MAX_COIN = 2;
    /**
     * 视频作者获取硬币的比例
     */
    private static final float VIDEO_COIN_HARVEST = 0.1f;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VideoCoin create(Long videoId, int count) {
        if (count < 1) {
            throw new CustomException("投币数量小于1");
        }
        Long contextUserId = SecurityUtils.getContextUserId(true);
        int coin = countCoin(videoId, contextUserId, null);
        if (coin + count > VIDEO_MAX_COIN) {
            throw new CustomException("达到投币上限");
        }
        //投币的目标用户
        Long targetUserId = DbUtils.getUserIdBy(Video.class, videoId);
        try {
            //减少投币方硬币
            DbUtils.increment(User::getCoin, count * -1, User::getId, contextUserId);
        } catch (Exception e) {
            throw new CustomException("硬币不足");
        }
        //增加被投币方硬币
        DbUtils.increment(User::getCoin, count * VIDEO_COIN_HARVEST, User::getId, targetUserId);

        VideoCoin entity = new VideoCoin();
        entity.setVideoId(videoId);
        entity.setCoinCount(count);
        if (save(entity)) {
            //发布事件
            publishEvent(new PayCoinEvent(this, entity));
        }
        return entity;
    }

    @Override
    @SuppressWarnings("all")
    public int countCoin(@Nullable Long videoId,
                  @Nullable Long userId,
                  @Nullable Period period) {
        VideoCoin one = wrapper()
                .selectSum(VideoCoin::getCoinCount, VideoCoin::getCoinCount) //写两次代表别名
                .eqIfExists(VideoCoin::getVideoId, videoId)
                .eqIfExists(VideoCoin::getUserId, userId)
                .and(period != null, w -> {
                    w.apply(String.format("%s(%s) = %s(now())",
                            period.getFunc(), "create_time", period.getFunc()));
                })
                .one(VideoCoin.class);
        return one != null ? one.getCoinCount() : 0;
    }

    @Override
    public int getUserCoin(Long videoId) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        VideoCoin one = wrapper().selectSum(VideoCoin::getCoinCount, VideoCoin::getCoinCount)
                .eq(VideoCoin::getVideoId, videoId)
                .eq(VideoCoin::getUserId, contextUserId)
                .one(VideoCoin.class);
        return one != null ? one.getCoinCount() : 0;
    }
}