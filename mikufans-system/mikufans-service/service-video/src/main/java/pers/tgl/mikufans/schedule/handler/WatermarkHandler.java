package pers.tgl.mikufans.schedule.handler;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.domain.video.VideoProcess;
import pers.tgl.mikufans.model.Watermark;
import pers.tgl.mikufans.schedule.VideoProcessHandler;
import pers.tgl.mikufans.util.FFmpegUtils;

import java.io.File;
import java.util.function.Consumer;

/**
 * 添加水印
 */
@Component
public class WatermarkHandler implements VideoProcessHandler {
    @Override
    public void handle(VideoProcess process, Consumer<Float> onProgress) throws Exception {
        Watermark watermark = process.getParams().getWatermark();
        if (watermark == null) {
            return;
        }
        File videoFile = process.getVideoFile();
        File outFile = new File(videoFile.getParentFile(), "watermark_" + videoFile.getName());
        File imgFile = new File(videoFile.getParentFile(), "watermark.png");
        Base64.decodeToFile(watermark.getData().replace("data:image/png;base64,", ""), imgFile);
        FFmpegUtils.overlayImage(videoFile, imgFile, outFile, watermark.getX(), watermark.getY());
        FileUtil.del(imgFile);
        //替换原文件
        FileUtil.rename(outFile, videoFile.getName(), true);
    }
}