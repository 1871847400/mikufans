package pers.tgl.mikufans.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.tgl.mikufans.domain.video.VideoWatchHistory;

import java.util.List;

/**
 * @author TGL
 * @description 针对表【video_watch_history(视频观看历史表)】的数据库操作Mapper
 * @createDate 2022-12-31 10:17:31
 * @Entity .domain.VideoWatchHistory
 */
@Mapper
public interface VideoWatchHistoryMapper extends MPJBaseMapper<VideoWatchHistory> {
    List<VideoWatchHistory> findByVideoId(long userId, String videoId);
}