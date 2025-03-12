package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.video.VideoShare;

public interface VideoShareService extends BaseService<VideoShare> {
    /**
     * 创建视频分享码
     */
    VideoShare createShare(Long vid);
    /**
     * 使用视频分享码(必须登录且不是分享码创建人)
     * 分享码第一次被使用时才会增加视频的分享次数
     */
    void useShare(String shareCode);
}