package pers.tgl.mikufans.listener;

import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.consts.ExpSource;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.event.*;
import pers.tgl.mikufans.service.UserService;
import pers.tgl.mikufans.util.DbUtils;
import pers.tgl.mikufans.util.RedisUtils;

@Component
@RequiredArgsConstructor
public class UserExpListener {
    private final UserService userService;
    private final RedisUtils redisUtils;

    /**
     * 限制每天通过某种方式获取的经验值
     */
    private void giveDailyExp(long userId, int exp, int dayExpLimit, ExpSource expSource) {
        String name = expSource.name().toLowerCase();
        String redisKey = "exp_daily_" + name + "_" + DateUtil.today() + ":" + userId;
        int getExp = Integer.parseInt(redisUtils.getString(redisKey, "0"));
        if (getExp + exp > dayExpLimit) {
            exp = dayExpLimit - getExp;
        }
        if (exp > 0) {
            //记录获取的经验值
            redisUtils.increment(redisKey, exp);
            //设置到期时间为明天0点
            redisUtils.expireAt(redisKey, DateUtil.beginOfDay(DateUtil.tomorrow()));
            userService.giveExp(userId, exp, expSource);
        }
    }

    @EventListener(VideoEvent.class)
    public void listen(VideoEvent e) {
        if (e.getAction() == EventAction.INSERT) {
            //每天第一次发布视频获得经验值
            giveDailyExp(e.getEntity().getUserId(), 50, 50, ExpSource.VIDEO);
        }
    }

    @EventListener(UserPublishEvent.class)
    public void listen(UserPublishEvent e) {
        if (e.getAction() == EventAction.INSERT) {
            //每天第一次发布说说获得经验值
            giveDailyExp(e.getEntity().getUserId(), 30, 30, ExpSource.PUBLISH);
        }
    }

    @EventListener(PayCoinEvent.class)
    public void listen(PayCoinEvent e) {
        Long userId = e.getVideoCoin().getUserId();
        Long targetId = DbUtils.getUserIdBy(Video.class, e.getVideoCoin().getVideoId());
        Integer count = e.getVideoCoin().getCoinCount();
        //双方都增加经验值
        userService.giveExp(targetId, count, ExpSource.COIN);
        //投币方每天获取经验值有上限
        giveDailyExp(userId, 10 * count, 50, ExpSource.COIN);
    }

    @EventListener(UserTokenEvent.class)
    public void listen(UserTokenEvent e) {
        if (e.getUserToken().getUserType() == 0) {
            giveDailyExp(e.getUserToken().getId(), 5, 5, ExpSource.LOGIN);
        }
    }
}