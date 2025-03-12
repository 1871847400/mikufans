package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.video.Video;

/**
* @author TGL
* @description 针对表【video_stats(视频统计信息表)】的数据库操作Service
* @createDate 2023-04-29 13:10:05
*/
public interface VideoStatsService extends BaseService<Video> {
    void addPlay(Long videoId, int value);
    void addCoin(Long videoId, int value);
    void addStar(Long videoId, int value);
    void addDanmu(Long videoId, int value);
    void addShare(Long videoId, int value);
}