package pers.tgl.mikufans.service.impl;

import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.video.VideoFlag;
import pers.tgl.mikufans.mapper.VideoFlagMapper;
import pers.tgl.mikufans.service.VideoFlagService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoFlagServiceImpl extends BaseServiceImpl<VideoFlag, VideoFlagMapper> implements VideoFlagService {
    @Override
    public Map<String, String> getVideoFlags(Long videoId, Long partId) {
        VideoFlag query = new VideoFlag();
        query.setVideoId(videoId);
        query.setPartId(partId);
        List<VideoFlag> videoFlags = listByEntity(query);
        Map<String, String> map = new LinkedHashMap<>();
        for (VideoFlag videoFlag : videoFlags) {
            map.put(videoFlag.getFlagName(), videoFlag.getFlagValue());
        }
        return map;
    }
}