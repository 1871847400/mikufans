package pers.tgl.mikufans.util;

import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DataUtils {
    private static final Tika tika = new Tika();

    /**
     * todo 准确性不高
     * 根据文件的magic标记判断媒体类型，如：audio/mpeg
     * 如果没有标记则根据后缀名判断
     * toString: audio/mpeg
     * type: audio
     * subType: mpeg
     * baseType: 自己
     */
    @Nullable
    public static MediaType getMediaType(File file) {
        try {
            return MediaType.parse(tika.detect(file));
        } catch (IOException e) {
            return null;
        }
    }
    @Nullable
    public static MediaType getMediaType(byte[] prefix) {
        try {
            return MediaType.parse(tika.detect(prefix));
        } catch (IllegalStateException e) {
            return null;
        }
    }
    @Nullable
    public static MediaType getMediaType(InputStream is) {
        try {
            return MediaType.parse(tika.detect(is));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 根据base64的前缀判断媒体类型
     * data:image/png;base64,iVBORw0KGg...
     */
    @Nullable
    public static MediaType getMediaType(String base64Text) {
        int index = base64Text.indexOf(",");
        if (index != -1) {
            String prefix = base64Text.substring(0, index);
            int start = prefix.indexOf(":") + 1;
            int end = prefix.indexOf(";");
            String type = prefix.substring(start, end);
            return MediaType.parse(type);
        }
        return null;
    }
}