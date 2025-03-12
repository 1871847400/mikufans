package pers.tgl.mikufans.service.impl;

import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.video.VideoShare;
import pers.tgl.mikufans.mapper.VideoShareMapper;
import pers.tgl.mikufans.service.VideoShareService;
import pers.tgl.mikufans.service.VideoStatsService;
import pers.tgl.mikufans.util.SecurityUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VideoShareServiceImpl extends BaseServiceImpl<VideoShare, VideoShareMapper> implements VideoShareService {
    private final VideoStatsService videoStatsService;

    @Override
    public VideoShare createShare(Long vid) {
        VideoShare videoShare = new VideoShare();
        videoShare.setVideoId(vid);
        videoShare.setUserId(SecurityUtils.getContextUserId(true));
        VideoShare old = lambdaQuery(videoShare).one();
        if (old != null) {
            return old;
        }
        videoShare.setShareCode(RandomUtil.randomString(8));
        videoShare.setShareCount(0);
        save(videoShare);
        return videoShare;
    }

    @Override
    public void useShare(String shareCode) {
        VideoShare videoShare = getOneBy(VideoShare::getShareCode, shareCode);
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (videoShare != null && contextUserId != null && !Objects.equals(contextUserId, videoShare.getUserId())) {
            incrementById(videoShare.getId(), VideoShare::getShareCount, 1);
            if (videoShare.getShareCount() == 0) {
                videoStatsService.addShare(videoShare.getVideoId(), 1);
            }
        }
    }
}