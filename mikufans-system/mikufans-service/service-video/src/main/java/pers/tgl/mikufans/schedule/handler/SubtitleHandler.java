package pers.tgl.mikufans.schedule.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.domain.video.VideoProcess;
import pers.tgl.mikufans.model.VideoPreprocess;
import pers.tgl.mikufans.schedule.VideoProcessHandler;
import pers.tgl.mikufans.util.FFmpegUtils;

import java.io.File;
import java.util.function.Consumer;

/**
 * 内嵌字幕
 */
@Component
public class SubtitleHandler implements VideoProcessHandler {
    @Override
    public void handle(VideoProcess process, Consumer<Float> onProgress) throws Exception {
        VideoPreprocess preprocess = process.getParams();
        if (StrUtil.isBlank(preprocess.getSubtitle())) {
            return;
        }
        File videoFile = process.getVideoFile();
        File subtitleFile = new File(videoFile.getParentFile(), "subtitle");
        FileUtil.writeUtf8String(preprocess.getSubtitle(), subtitleFile);
        File outFile = new File(videoFile.getParentFile(), "subtitle_" + videoFile.getName());
        FFmpegUtils.addSubtitleStream(videoFile, subtitleFile, outFile);
        FileUtil.del(subtitleFile);
        FileUtil.rename(outFile, videoFile.getName(), true);
    }
}