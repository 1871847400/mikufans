package pers.tgl.mikufans.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MediaInfo implements Serializable {
    private List<Stream> streams;
    private Format format;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Stream {
        private int index;
        //h264
        private String codecName;
        //H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10
        private String codecLongName;
        //High
        private String profile;
        private String codecType; // video or audio
        private String codecTagString;
        private int width;
        private int height;
        // 34080046
        private long durationTs;
        // 1420.001917(秒)
        private String duration;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Format {
        private String filename;
        private String formatName;
        // 1420.061995
        private String duration;
        private String size;
        private String bitRate;
        private FormatTags tags;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class FormatTags {
        private String encoder;
    }
}