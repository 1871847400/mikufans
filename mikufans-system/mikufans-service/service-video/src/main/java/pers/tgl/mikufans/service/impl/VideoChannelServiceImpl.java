package pers.tgl.mikufans.service.impl;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.video.VideoChannel;
import pers.tgl.mikufans.mapper.VideoChannelMapper;
import pers.tgl.mikufans.params.VideoChannelParams;
import pers.tgl.mikufans.service.VideoChannelService;

import java.util.List;

@Service
public class VideoChannelServiceImpl extends BaseServiceImpl<VideoChannel, VideoChannelMapper>
        implements VideoChannelService {
    @Override
    public List<VideoChannel> search(VideoChannelParams search) {
        List<VideoChannel> list = getList(0L, search.getSize());
        if (search.isChild()) {
            for (VideoChannel channel : list) {
                channel.setChildren(getList(channel.getId(), null));
            }
        }
        return list;
    }

    @Override
    public VideoChannel getOneWithChildren(Long id) {
        VideoChannel channel = getById(id);
        if (channel == null) {
            return null;
        }
        channel.setChildren(listBy(VideoChannel::getPid, channel.getId()));
        return channel;
    }

    private List<VideoChannel> getList(Long pid, @Nullable Integer size) {
        return wrapper()
                .eq(VideoChannel::getDisabled, 0)
                .eq(VideoChannel::getPid, pid)
                .orderByAsc(VideoChannel::getSort)
                .orderByDesc(VideoChannel::getCreateTime)
                .last(size != null && size >= 0, "limit " + size)
                .list();
    }
}