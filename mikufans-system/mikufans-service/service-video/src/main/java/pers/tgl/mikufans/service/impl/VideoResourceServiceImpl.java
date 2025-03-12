package pers.tgl.mikufans.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import io.lindstrom.m3u8.parser.PlaylistParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.consts.TransferMode;
import pers.tgl.mikufans.domain.enums.VideoQuality;
import pers.tgl.mikufans.domain.video.VideoResource;
import pers.tgl.mikufans.event.EventAction;
import pers.tgl.mikufans.event.VideoPartEvent;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.VideoResourceMapper;
import pers.tgl.mikufans.service.VideoResourceService;
import pers.tgl.mikufans.transfer.ResourceTransfer;
import pers.tgl.mikufans.transfer.ResourceTransferProvider;
import pers.tgl.mikufans.util.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TGL
 * @description 针对表【video_resource(视频资源表)】的数据库操作Service实现
 * @createDate 2023-01-16 13:56:59
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VideoResourceServiceImpl extends BaseServiceImpl<VideoResource, VideoResourceMapper>
        implements VideoResourceService {
    private final RedisUtils redisUtils;
    private final ResourceTransferProvider resourceTransferProvider;

    @Override
    public File getStorage(Long resId, @Nullable String child) {
        String localPath = getColumnValue(resId, VideoResource::getLocalPath);
        if (StrUtil.isBlank(localPath)) {
            return new File("");
        }
        return StrUtil.isNotBlank(child) ? new File(localPath, child) : new File(localPath);
    }

    @Nullable
    @Override
    public MediaPlaylist getMediaPlaylist(long resId, @Nullable VideoQuality quality) {
        String folderName = null;
        if (quality != null) {
            folderName = quality.name().toLowerCase();
        } else {
            for (VideoQuality q : VideoQuality.values()) {
                File file = getStorage(resId, q.key());
                if (file.exists()) {
                    folderName = file.getName();
                    break;
                }
            }
        }
        if (StrUtil.isBlank(folderName)) {
            return null;
        }
        String redisKey = "playlist:" + resId + ":" + folderName;
        String cache = redisUtils.getString(redisKey, "");
        if (StrUtil.isNotBlank(cache)) {
            try {
                return new MediaPlaylistParser().readPlaylist(cache);
            } catch (PlaylistParserException e) {
                log.error("playlist parser error", e);
                redisUtils.del(redisKey);
                return null;
            } finally {
                redisUtils.expire(redisKey, Duration.ofHours(1));
            }
        } else {
            File playlistFile = getStorage(resId, folderName + File.separator + "playlist.m3u8");
            redisUtils.set(redisKey, FileUtil.readUtf8String(playlistFile), Duration.ofHours(1));
            //MediaPlaylist不支持序列化
            return FFmpegUtils.parseM3U8File(playlistFile);
//            if (mediaPlaylist != null) {
//                m3U8Info = new M3U8Info();
//                m3U8Info.setTargetDuration(mediaPlaylist.targetDuration());
//                Map<String, M3U8Info.Segment> segments = new HashMap<>();
//                double start = 0;
//                for (MediaSegment mediaSegment : mediaPlaylist.mediaSegments()) {
//                    M3U8Info.Segment segment = new M3U8Info.Segment();
//                    segment.setUri(mediaSegment.uri());
//                    segment.setDuration(mediaSegment.duration());
//                    segment.setStart(start);
//                    segment.setEnd(start + mediaSegment.duration());
//                    segments.put(mediaSegment.uri(), segment);
//                    start += mediaSegment.duration();
//                }
//                m3U8Info.setSegments(segments);
//                redisUtils.set(key, m3U8Info, Duration.ofHours(1));
//            }
        }
    }

    @Override
    public void fetchSegment(VideoResource resource, VideoQuality quality, String uri) throws IOException {
        HttpServletResponse response = ServletUtils.getResponse();
        //防止访问到非法资源
        if (!uri.endsWith(".ts")) {
            throw new CustomException("权限不足");
        }
        //优先使用转存的资源
        TransferMode mode = resource.getTransferMode();
        ResourceTransfer resourceTransfer = resourceTransferProvider.getResourceTransfer(mode);
        if (resourceTransfer != null) {
            String filePath = resource.getTransferPath() + "/" + quality.key() + "/" + uri;
            List<String> links = resourceTransfer.getDownloadLinks(filePath);
            if (CollUtil.isNotEmpty(links)) {
                if (mode == TransferMode.OSS) {
                    response.sendRedirect(links.get(0));
                    return;
                }
                Map<String, Object> result = new LinkedHashMap<>(2);
                result.put("mode", mode);
                result.put("links", links);
                MyUtils.writePlainText(response, JsonUtils.writeString(result));
                return;
            }
        }
        //尝试使用本地文件
        String path = quality.key() + File.separator + uri;
        File file = getStorage(resource.getId(), path);
        if (!file.exists()) {
            throw new CustomException("视频文件不存在");
        }
        if (StrUtil.isNotBlank(resource.getMediaType())) {
            response.setContentType(resource.getMediaType());
        } else {
            response.setContentType(MyUtils.getMimeType(file));
        }
        response.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=2592000");
        IoUtil.write(response.getOutputStream(), true, FileUtil.readBytes(file));
    }

    @Override
    public boolean removeById(VideoResource entity) {
        if (super.removeById(entity)) {
            File storage = getStorage(entity.getId(), null);
            if (!FileUtil.del(storage)) {
                log.error("删除资源目录出现错误 {} ", storage.getAbsolutePath());
            }
            ResourceTransfer resourceTransfer = resourceTransferProvider.getResourceTransfer(entity.getTransferMode());
            if (resourceTransfer != null) {
                try {
                    resourceTransfer.delete(entity.getTransferPath());
                } catch (IOException e) {
                    log.error("删除转存视频文件出现错误", e);
                }
            }
            return true;
        }
        return false;
    }

    @EventListener(VideoPartEvent.class)
    public void listen(VideoPartEvent e) {
        if (e.getAction() == EventAction.DELETE) {
            removeById(e.getEntity().getResId());
        }
    }
}