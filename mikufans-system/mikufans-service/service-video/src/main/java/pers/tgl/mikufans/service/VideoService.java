package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.dto.VideoDto;
import pers.tgl.mikufans.vo.VideoVo;

/**
* @author TGL
* @description 针对表【video(视频表)】的数据库操作Service
* @createDate 2022-12-31 10:17:31
*/
public interface VideoService extends BaseService<Video> {
    /**
     * 创建视频
     */
    Long createVideo(VideoDto dto);
    /**
     * 更新视频
     */
    void updateVideo(VideoDto dto);
    /**
     * 通过id或sid获取视频信息
     */
    VideoVo getVoById(Object id);
    /**
     * 申请解封视频
     * 1.将所有审核通过的视频转为未审核
     * 2.解封
     */
    void apply(Long videoId);
}