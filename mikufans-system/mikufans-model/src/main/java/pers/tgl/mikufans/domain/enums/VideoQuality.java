package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * 视频和音频的质量级别
 */
@Getter
@RequiredArgsConstructor
public enum VideoQuality {
    /**
     * 需要按等级从小到大排列
     */
    SD(1, "流畅", 1 * 1024 * 1024, 48 * 1024),
    HD(2, "高清",  2 * 1024 * 1024, 128 * 1024),
    FHD(3, "超清", 3 * 1024 * 1024, 512 * 1024);

    //画质级别
    @EnumValue
    private final Integer level;
    //名称
    private final String name;
    /**
     * 视频最大码率,如果用字符串表示,m必须大写
     * 分片时-b码率不能超过实际码率
     */
    private final int videoBitrate;
    /**
     * 音频最大码率
     */
    private final int audioBitrate;
    /**
     * 作为标识符
     */
    public String key() {
        return this.name().toLowerCase();
    }

    @Nullable
    public static VideoQuality fromLevel(int level) {
        for (VideoQuality quality : values()) {
            if (Objects.equals(quality.level, level)) {
                return quality;
            }
        }
        return null;
    }
}