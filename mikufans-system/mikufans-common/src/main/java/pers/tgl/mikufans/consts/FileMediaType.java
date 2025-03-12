package pers.tgl.mikufans.consts;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

@Getter
@AllArgsConstructor
public enum FileMediaType {
    VIDEO("video"),
    AUDIO("audio"),
    IMAGE("image"),
    UNKNOWN("unknown");

    @EnumValue
    private final String key;

    public static FileMediaType getType(@Nullable String mediaType) {
        return mediaType != null ? getType(MediaType.parseMediaType(mediaType)) : UNKNOWN;
    }

    public static FileMediaType getType(@Nullable MediaType mediaType) {
        if (mediaType == null) {
            return FileMediaType.UNKNOWN;
        }
        switch (mediaType.getType()) {
            case "video":
                return VIDEO;
            case "audio":
                return AUDIO;
            case "image":
                return IMAGE;
        }
        return FileMediaType.UNKNOWN;
    }
}