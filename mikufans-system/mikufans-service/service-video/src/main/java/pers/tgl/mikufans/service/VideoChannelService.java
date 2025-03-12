package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.video.VideoChannel;
import pers.tgl.mikufans.params.VideoChannelParams;

import java.util.List;

public interface VideoChannelService extends BaseService<VideoChannel> {
    /**
     * 搜索所有分区
     */
    List<VideoChannel> search(VideoChannelParams search);
    /**
     * 如果是父分区会带上所有子分区
     */
    VideoChannel getOneWithChildren(Long id);
}