package pers.tgl.mikufans.schedule.handler;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.config.upload.UploadConfig;
import pers.tgl.mikufans.consts.FileMediaType;
import pers.tgl.mikufans.domain.enums.VideoType;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoProcess;
import pers.tgl.mikufans.domain.video.VideoResource;
import pers.tgl.mikufans.schedule.VideoProcessHandler;
import pers.tgl.mikufans.service.VideoResourceService;
import pers.tgl.mikufans.service.VideoService;
import pers.tgl.mikufans.util.FFmpegUtils;
import pers.tgl.mikufans.util.HlsConvertResult;

import java.io.File;
import java.util.function.Consumer;

/**
 * 视频分片处理器
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class HlsHandler implements VideoProcessHandler {
    private final VideoService videoService;
    private final VideoResourceService videoResourceService;
    private final AppConfig appConfig;

    @Override
    public void handle(VideoProcess process, Consumer<Float> onProgress) throws Exception {
        File videoFile = process.getVideoFile();
        UploadConfig config = appConfig.getUpload().get(FileMediaType.VIDEO);
        VideoType videoType = videoService.getColumnValue(process.getVideoId(), Video::getType);
        if (videoType == null) {
            throw new Exception("视频不存在 vid=" + process.getVideoId());
        }
        HlsConvertResult result = FFmpegUtils.convertToHls(videoFile, videoFile.getParentFile(),
                config.isEncrypt(), onProgress);
        VideoResource videoResource = new VideoResource();
        videoResource.setId(process.getResId());
        videoResource.setQualityLevel(result.getMaxLevelQuality().getLevel());
        Db.updateById(videoResource);
    }
}