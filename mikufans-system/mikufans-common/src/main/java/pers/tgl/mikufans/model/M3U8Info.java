package pers.tgl.mikufans.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 对m3u8文件进行必要的分析后进行缓存
 * 由于MediaPlaylist不能反序列化所以用此类代替
 */
@Data
@Deprecated
public class M3U8Info implements Serializable {
    /**
     * 每个分片的目标持续时间 hls_time
     */
    private int targetDuration;

    /**
     * 片段路径 : 持续时间
     */
    private Map<String, Segment> segments;

    @Data
    public static final class Segment {
        private String uri;
        private double duration;
        private double start;
        private double end;
    }
}