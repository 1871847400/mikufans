package pers.tgl.mikufans.service.impl;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.comment.UserCommentArea;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.user.UserLikeData;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoCoin;
import pers.tgl.mikufans.event.PayCoinEvent;
import pers.tgl.mikufans.event.UserLikeEvent;
import pers.tgl.mikufans.event.VideoStarEvent;
import pers.tgl.mikufans.mapper.VideoMapper;
import pers.tgl.mikufans.service.UserCommentAreaService;
import pers.tgl.mikufans.service.UserLikeDataService;
import pers.tgl.mikufans.service.VideoStatsService;
import pers.tgl.mikufans.util.RedisUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author TGL
 * @description 针对表【video_stats(视频统计信息表)】的数据库操作Service实现
 * @createDate 2023-04-29 13:10:05
 */
@Service
@RequiredArgsConstructor
public class VideoStatsServiceImpl extends BaseServiceImpl<Video, VideoMapper> implements VideoStatsService {
    private static final String REDIS_KEY = "video-stats-score";

    private final RedisUtils redisUtils;
    private final UserCommentAreaService userCommentAreaService;
    private final UserLikeDataService userLikeDataService;

    private void increment(Long videoId, SFunction<Video, Number> column, Number value) {
        incrementById(videoId, column, value);
        redisUtils.sadd(REDIS_KEY, videoId);
    }

    @Override
    public void addPlay(Long videoId, int value) {
        increment(videoId, Video::getPlays, value);
    }

    @Override
    public void addCoin(Long videoId, int value) {
        increment(videoId, Video::getCoins, value);
    }

    @Override
    public void addStar(Long videoId, int value) {
        increment(videoId, Video::getStars, value);
    }

    @Override
    public void addDanmu(Long videoId, int value) {
        increment(videoId, Video::getDanmus, value);
    }

    @Override
    public void addShare(Long videoId, int value) {
        increment(videoId, Video::getShares, value);
    }

    @EventListener(value = PayCoinEvent.class)
    public void listen(PayCoinEvent event) {
        VideoCoin coin = event.getVideoCoin();
        addCoin(coin.getVideoId(), coin.getCoinCount());
    }

    @EventListener(VideoStarEvent.class)
    public void listen(VideoStarEvent e) {
        Long videoId = e.getEntity().getVideoId();
        if (e.getStarCount() == 0 && e.isDelete()) {
            addStar(videoId, -1);
        } else if (e.getStarCount() == 1 && e.isInsert()) {
            addStar(videoId, 1);
        }
    }

    @EventListener(UserLikeEvent.class)
    public void listen(UserLikeEvent e) {
        if (e.getLikeData().getLikeType() == BusinessType.VIDEO) {
            redisUtils.sadd(REDIS_KEY, e.getLikeData().getBusiId());
        }
    }

    /**
     * 定期重新计算热度分数
     */
    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void calcScore() {
        Set<Object> videoIds = redisUtils.smemebers(REDIS_KEY);
        redisUtils.srem(REDIS_KEY, videoIds);
        for (Object id : videoIds) {
            Long videoId = Long.parseLong(id + "");
            Video video = findById(videoId);
            UserLikeData likeData = userLikeDataService.getLikeData(videoId);
            UserCommentArea commentArea = userCommentAreaService.getCommentArea(videoId);
            if (video == null || likeData == null || commentArea == null) {
                continue;
            }
            double score = 0;
            score += video.getPlays() * 0.1;
            score += video.getCoins() * 0.5;
            score += video.getStars() * 3;
            score += video.getDanmus() * 0.05;
            score += video.getShares() * 3;
            score += likeData.getLikes() * 0.2;
            score += likeData.getDislikes() * -0.5;
            score += commentArea.getComments() * 1.2;
            updateById(video.getId(), Video::getScore, score);
        }
    }
}