package pers.tgl.mikufans.schedule.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.domain.video.VideoProcess;
import pers.tgl.mikufans.model.VideoPreprocess;
import pers.tgl.mikufans.model.VideoSegment;
import pers.tgl.mikufans.schedule.VideoProcessHandler;
import pers.tgl.mikufans.util.FFmpegUtils;

import java.io.File;
import java.util.function.Consumer;

/**
 * 分割出所有片段再依次合并为一个新的视频
 */
@Component
public class SegmentHandler implements VideoProcessHandler {
    @Override
    public void handle(VideoProcess process, Consumer<Float> onProgress) throws Exception {
        VideoPreprocess preprocess = process.getParams();
        if (CollUtil.isEmpty(preprocess.getSegments())) {
            return;
        }
        File videoFile = process.getVideoFile();
        File lastFile = null;
        int index = 0;
        for (VideoSegment segment : preprocess.getSegments()) {
            File outFile = new File(videoFile.getParentFile(), "seg_" + index + "_" + videoFile.getName());
            FFmpegUtils.separate(videoFile, outFile, segment.getOffset(), segment.getLength());
            if (lastFile != null) {
                FFmpegUtils.merge(lastFile, outFile, lastFile);
                FileUtil.del(outFile);
            } else {
                lastFile = outFile;
            }
            index++;
        }
        if (lastFile != null) {
            FileUtil.rename(lastFile, videoFile.getName(), true);
        }
    }
}