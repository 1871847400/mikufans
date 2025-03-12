package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum SubtitleType {
    UNKNOWN, SRT, ASS, VTT;

    @EnumValue
    private final String code;

    SubtitleType() {
        this.code = name().toLowerCase();
    }

    /**
     * 文件后缀
     */
    public String getSuffix() {
        return this.name().toLowerCase();
    }

    public static SubtitleType parse(String filename) {
        for (SubtitleType type : values()) {
            if (filename != null && filename.toLowerCase().endsWith("." + type.name().toLowerCase())) {
                return type;
            }
        }
        return UNKNOWN;
    }
}