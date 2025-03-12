package pers.tgl.mikufans.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.event.*;
import pers.tgl.mikufans.mapper.UserMapper;
import pers.tgl.mikufans.service.UserStatsService;
import pers.tgl.mikufans.util.DbUtils;

@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl extends BaseServiceImpl<User, UserMapper> implements UserStatsService {
    @EventListener(PayCoinEvent.class)
    public void listen(PayCoinEvent e) {
        //被投币的用户
        Long userId = DbUtils.getUserIdBy(Video.class, e.getVideoCoin().getVideoId());
        int count = e.getVideoCoin().getCoinCount();
        //累计用户收到的硬币
        incrementById(userId, User::getCoin, count);
    }

    @EventListener(VideoEvent.class)
    public void listen(VideoEvent e) {
        Long userId = e.getEntity().getUserId();
        if (e.getAction() == EventAction.INSERT) {
            incrementById(userId, User::getVideos, 1);
        } else if (e.getAction() == EventAction.DELETE) {
            incrementById(userId, User::getVideos, -1);
        }
    }

    @EventListener(UserPublishEvent.class)
    public void listen(UserPublishEvent e) {
        Long userId = e.getEntity().getUserId();
        if (e.getAction() == EventAction.INSERT) {
            incrementById(userId, User::getPublishes, 1);
        } else if (e.getAction() == EventAction.DELETE) {
            incrementById(userId, User::getPublishes, -1);
        }
    }

    @EventListener(UserDynamicEvent.class)
    public void listen(UserDynamicEvent e) {
        Long userId = e.getEntity().getUserId();
        if (e.getAction() == EventAction.INSERT) {
            incrementById(userId, User::getDynamics, 1);
        } else if (e.getAction() == EventAction.DELETE) {
            incrementById(userId, User::getDynamics, -1);
        }
    }

    @EventListener(SubscribeEvent.class)
    public void listen(SubscribeEvent event) {
        Long userId = event.getSubscribe().getUserId();
        incrementById(userId, User::getSubscribes, event.isDelete() ? -1 : 1);
    }

    @EventListener(FollowEvent.class)
    @Transactional(rollbackFor = Exception.class)
    public void listen(FollowEvent event) {
        Long userId = event.getUserFollow().getUserId();
        Long targetId = event.getUserFollow().getTargetId();
        incrementById(userId, User::getFollows, event.isDelete() ? -1 : 1);
        incrementById(targetId, User::getFans, event.isDelete() ? -1 : 1);
    }

    @EventListener(UserArticleEvent.class)
    public void listen(UserArticleEvent e) {
        Long userId = e.getArticle().getUserId();
        if (e.getEventAction() == EventAction.INSERT) {
            incrementById(userId, User::getArticles, 1);
        } else if (e.getEventAction() == EventAction.DELETE) {
            incrementById(userId, User::getArticles, -1);
        }
    }
}