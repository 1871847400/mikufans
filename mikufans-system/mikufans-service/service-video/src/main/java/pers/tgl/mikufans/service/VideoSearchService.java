package pers.tgl.mikufans.service;

import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.params.VideoParams;
import pers.tgl.mikufans.params.VideoUploadParams;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.vo.HighlightText;
import pers.tgl.mikufans.vo.VideoVo;

import java.util.List;

public interface VideoSearchService extends BaseService<pers.tgl.mikufans.domain.video.Video> {
    /**
     * 综合搜索服务
     */
    PageImpl<VideoVo> search(VideoParams params);
    /**
     * 最近点赞的视频列表
     */
    PageImpl<Video> getLikeVideos(@Nullable Long userId);
    /**
     * 获取自己上传的视频稿件列表
     */
    PageImpl<VideoVo> getUploadList(VideoUploadParams params);
    /**
     * 获取搜索词建议
     */
    List<HighlightText> getCompletions(String prefix, int size);
    /**
     * 获得随机视频推荐
     */
    List<VideoVo> getRecommend(int size);
    /**
     * 获得相关推荐
     */
    List<VideoVo> getRelateList(Long videoId, int size);
}