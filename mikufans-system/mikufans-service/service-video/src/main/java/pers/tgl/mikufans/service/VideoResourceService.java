package pers.tgl.mikufans.service;

import io.lindstrom.m3u8.model.MediaPlaylist;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.enums.VideoQuality;
import pers.tgl.mikufans.domain.video.VideoResource;

import java.io.File;
import java.io.IOException;

/**
* @author TGL
* @description 针对表【video_resource(视频资源表)】的数据库操作Service
* @createDate 2023-01-16 13:56:59
*/
public interface VideoResourceService extends BaseService<VideoResource> {
    /**
     * 用户下载视频分片
     * @param uri ts切片名称 0.ts
     */
    void fetchSegment(VideoResource resource, VideoQuality quality, String uri) throws IOException;
    /**
     * 获取资源目录的文件
     * 每一个资源id对应一个文件夹,文件夹内存放视频,音频,字幕等文件
     */
    File getStorage(Long resId, @Nullable String child);
    /**
     * 获取视频资源的播放列表信息
     * @param quality 不指定则随机
     * @return m3u8文件的解析信息
     */
    MediaPlaylist getMediaPlaylist(long resId, @Nullable VideoQuality quality);
}