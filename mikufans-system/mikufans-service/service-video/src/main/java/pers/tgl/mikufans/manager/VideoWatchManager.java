package pers.tgl.mikufans.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.dto.VideoWatchDto;
import pers.tgl.mikufans.model.VideoWatchStatus;
import pers.tgl.mikufans.util.RedisUtils;
import pers.tgl.mikufans.util.SecurityUtils;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class VideoWatchManager {
    private final RedisUtils redisUtils;

    /**
     * 缓存当前已登录用户正在看的视频
     * @return 返回新增已观看的时长
     */
    public int record(VideoWatchDto dto) {
        int diffTime = 0;
        Long contextUserId = SecurityUtils.getContextUserId(true);
        long now = System.currentTimeMillis();
        String redisKey = "watching:" + dto.getVideoId() + ":" + contextUserId;
        VideoWatchStatus videoWatchStatus = redisUtils.getObject(redisKey, VideoWatchStatus.class);
        if (videoWatchStatus == null) {
            videoWatchStatus = new VideoWatchStatus();
            videoWatchStatus.setUserId(contextUserId);
            videoWatchStatus.setVideoId(dto.getVideoId());
        } else {
            diffTime = (int) (now - videoWatchStatus.getUpdateTime());
        }
        videoWatchStatus.setPartId(dto.getPartId());
        videoWatchStatus.setWatchPos(dto.getWatchPos());
        videoWatchStatus.setUpdateTime(now);
        redisUtils.set(redisKey, videoWatchStatus, Duration.ofSeconds(45));
        return diffTime;
    }

    /**
     * 当前正在观看某视频的人数(包括游客)
     */
    public int getOnline(long videoId) {
        return redisUtils.keys("watching:" + videoId + ":*").size();
    }
}