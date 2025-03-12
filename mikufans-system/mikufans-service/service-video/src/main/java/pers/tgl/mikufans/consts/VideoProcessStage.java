package pers.tgl.mikufans.consts;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pers.tgl.mikufans.schedule.VideoProcessHandler;
import pers.tgl.mikufans.schedule.handler.*;

import java.util.Arrays;

/**
 * 视频处理流程,必须按顺序
 */
@Getter
@RequiredArgsConstructor
public enum VideoProcessStage {
    SEGMENT(1, 20, SegmentHandler.class),
    SUBTITLE(2, 5, SubtitleHandler.class),
    WATERMARK(3, 5, WatermarkHandler.class),
    THUMBNAIL(4, 5, ThumbnailHandler.class),
    HLS(5, 200, HlsHandler.class),
    TRANSFER(6, 100, TransferHandler.class),
    DONE(100, 1, DoneHandler.class);

    @EnumValue
    private final int code;
    /**
     * 时间占比权重
     */
    private final int power;

    private final Class<? extends VideoProcessHandler> handlerClass;
    /**
     * 在这之前阶段的总权重
     */
    public int getBeforePowers() {
        int temp = 0;
        for (VideoProcessStage stage : VideoProcessStage.values()) {
            if (this.ordinal() > stage.ordinal()) {
                temp += stage.power;
            } else {
                break;
            }
        }
        return temp;
    }

    public static int getTotalPower() {
        return Arrays.stream(values()).mapToInt(VideoProcessStage::getPower).sum();
    }

    public static VideoProcessStage fromCode(int code) {
        for (VideoProcessStage stage : VideoProcessStage.values()) {
            if (stage.code == code) {
                return stage;
            }
        }
        return null;
    }
}