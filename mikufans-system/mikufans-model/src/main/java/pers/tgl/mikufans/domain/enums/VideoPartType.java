package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VideoPartType {
    TV(1);

    @EnumValue
    private final int code;

    public static VideoPartType fromCode(int code) {
        for (VideoPartType type : VideoPartType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}