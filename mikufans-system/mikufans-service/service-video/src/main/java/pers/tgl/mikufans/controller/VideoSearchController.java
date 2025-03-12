package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.consts.VideoStatus;
import pers.tgl.mikufans.domain.enums.VideoType;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoChannel;
import pers.tgl.mikufans.model.QueryOption;
import pers.tgl.mikufans.params.VideoChannelParams;
import pers.tgl.mikufans.params.VideoParams;
import pers.tgl.mikufans.params.VideoUploadParams;
import pers.tgl.mikufans.search.SearchDateRange;
import pers.tgl.mikufans.search.VideoDurationRange;
import pers.tgl.mikufans.search.VideoSearchSort;
import pers.tgl.mikufans.service.VideoChannelService;
import pers.tgl.mikufans.service.VideoSearchService;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.vo.HighlightText;
import pers.tgl.mikufans.vo.VideoVo;

import javax.validation.constraints.Max;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/video")
@Validated
@RequiredArgsConstructor
public class VideoSearchController extends BaseController {
    private final VideoSearchService videoSearchService;
    private final VideoChannelService videoChannelService;
    /**
     * 根据条件获得一页视频(必须至少包含一个有效资源)
     */
    @GetMapping("/search")
    public IPage<VideoVo> search(@Validated VideoParams params) {
        return videoSearchService.search(params);
    }
    /**
     * 最近点赞的视频列表
     */
    @GetMapping("/like/{userId}")
    public IPage<Video> getLikeVideos(@PathVariable Long userId) {
        return videoSearchService.getLikeVideos(userId);
    }
    /**
     * 获取随机推荐视频
     */
    @GetMapping("/recommend")
    public List<VideoVo> getRecommend(@RequestParam(defaultValue = "10") @Max(20) int size) {
        return videoSearchService.getRecommend(size);
    }
    /**
     * 搜索词补全
     */
    @GetMapping("/completion")
    public List<HighlightText> getCompletion(@RequestParam String prefix) {
        return videoSearchService.getCompletions(prefix, 10);
    }
    /**
     * 搜索最近点赞的视频
     */
    @GetMapping("/favors")
    public Page<Video> getFavors(@RequestParam(required = false) Long userId) {
        return videoSearchService.getLikeVideos(userId);
    }
    /**
     * 获取自己投稿的视频
     */
    @GetMapping("/upload")
    public PageImpl<VideoVo> getUploadList(@Validated VideoUploadParams params) {
        return videoSearchService.getUploadList(params);
    }
    /**
     * 获取各个状态的视频数量
     */
    @GetMapping("/upload/count")
    public Map<VideoStatus, Long> getUploadCount(@RequestParam(required = false) VideoType type) {
        Map<VideoStatus, Long> map = new HashMap<>();
        VideoUploadParams params = new VideoUploadParams();
        params.setVideoType(type);
        params.setPageNum(1);
        params.setPageSize(0);
        for (VideoStatus status : VideoStatus.values()) {
            params.setStatus(status);
            long total = videoSearchService.getUploadList(params).getTotal();
            map.put(status, total);
        }
        return map;
    }
    /**
     * 获取相关视频
     */
    @GetMapping("/relate/{videoId}")
    public List<VideoVo> getRelatedVideos(@PathVariable Long videoId,
                                          @RequestParam(defaultValue = "10") @Range(min = 0, max = 100) Integer size) {
        return videoSearchService.getRelateList(videoId, size);
    }
    /**
     * 排序选项
     */
    @GetMapping("/search/options")
    @Cacheable(value = "video-search-options#1h")
    public List<QueryOption> getOptions() {
        List<QueryOption> options = new ArrayList<>();
        QueryOption sort = new QueryOption("sort", VideoSearchSort.SCORE.name());
        for (VideoSearchSort value : VideoSearchSort.values()) {
            sort.addOption(value.getDisplay(), value.name());
        }
        options.add(sort);
        QueryOption date = new QueryOption("date", "");
        date.addOption("全部日期", "");
        for (SearchDateRange value : SearchDateRange.values()) {
            date.addOption(value.getLabel(), value.name());
        }
        options.add(date);
        QueryOption duration = new QueryOption("duration", VideoDurationRange.ALL.name());
        for (VideoDurationRange value : VideoDurationRange.values()) {
            duration.addOption(value.getLabel(), value.name());
        }
        options.add(duration);
        QueryOption channelId = new QueryOption("channelId", "");
        channelId.addOption("全部分区", "");
        List<VideoChannel> videoChannels = videoChannelService.search(new VideoChannelParams());
        for (VideoChannel videoChannel : videoChannels) {
            channelId.addOption(videoChannel.getChannelName(), videoChannel.getId());
        }
        options.add(channelId);
        return options;
    }
}