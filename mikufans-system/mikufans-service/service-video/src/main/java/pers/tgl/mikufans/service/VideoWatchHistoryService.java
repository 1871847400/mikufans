package pers.tgl.mikufans.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.tgl.mikufans.domain.video.VideoWatchHistory;
import pers.tgl.mikufans.dto.VideoWatchDto;
import pers.tgl.mikufans.params.HistorySearch;
import pers.tgl.mikufans.vo.VideoWatchHistoryVo;

/**
* @author TGL
* @description 针对表【video_watch_history(视频观看历史表)】的数据库操作Service
* @createDate 2022-12-31 10:17:31
*/
public interface VideoWatchHistoryService extends BaseService<VideoWatchHistory> {
    /**
     * 搜索当前用户在指定视频的观看历史
     */
    VideoWatchHistoryVo getVoByVid(Long videoId);
    /**
     * 搜索用户的播放历史
     */
    IPage<VideoWatchHistoryVo> search(HistorySearch params);
    /**
     * 1.记录哪些用户正在播放哪些视频(一个用户可能同时观看多个视频)
     * 2.记录播放位置
     * 3.记录播放时长,需要用户在指定超时时间内发送请求,以此增加间隔时间作为播放时间
     */
    void create(VideoWatchDto dto);
    /**
     * 删除用户的所有观看历史
     */
    void removeAllByUserId();
    /**
     * 获取当前用户的观看记录
     */
    VideoWatchHistory getCurrentHistory(Long vid);
}