package pers.tgl.mikufans.schedule.handler;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.config.upload.UploadConfig;
import pers.tgl.mikufans.consts.FileMediaType;
import pers.tgl.mikufans.domain.video.VideoProcess;
import pers.tgl.mikufans.domain.video.VideoResource;
import pers.tgl.mikufans.schedule.VideoProcessHandler;
import pers.tgl.mikufans.util.FFmpegUtils;

import java.io.File;
import java.time.Duration;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class DoneHandler implements VideoProcessHandler {
    private final AppConfig appConfig;

    @Override
    public void handle(VideoProcess process, Consumer<Float> onProgress) throws Exception {
        File videoFile = process.getVideoFile();
        long duration = FFmpegUtils.parseMediaFileDuration(videoFile, Duration.ZERO).toMillis();
        UploadConfig config = appConfig.getUpload().get(FileMediaType.VIDEO);
        VideoResource resource = new VideoResource();
        resource.setId(process.getResId());
        //最终的视频时长
        resource.setDuration((int) duration);
        //设置为可用状态
        resource.setPending(0);
        //更新数据库
        Db.updateById(resource);
        //删除源文件
        if (config.getTransfer().isDeleteOnSuccess()) {
            FileUtil.del(videoFile);
            log.info("已删除原始文件 {}", videoFile.getAbsolutePath());
        }
    }
}