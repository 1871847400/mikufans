package pers.tgl.mikufans.service;

import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.video.VideoFlag;

import java.util.Map;

public interface VideoFlagService extends BaseService<VideoFlag> {
    Map<String, String> getVideoFlags(Long videoId, @Nullable Long partId);
}