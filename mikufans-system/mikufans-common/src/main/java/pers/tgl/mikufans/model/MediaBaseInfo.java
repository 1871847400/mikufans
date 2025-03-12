package pers.tgl.mikufans.model;

import lombok.Data;

import java.time.Duration;

/**
 * ffmpeg -i 获取的信息
 */
@Data
public class MediaBaseInfo {
    private final Duration duration;
    private final int fps;

    /**
     * 一共多少帧
     */
    public long getFrame() {
        return (long) (duration.toMillis() / 1000D * fps);
    }
}