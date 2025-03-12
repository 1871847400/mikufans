package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Deprecated
public enum UserDynamicType {
    VIDEO_PART(1),
    PUBLISH(2),
    RATE(3),
    ARTICLE(4);

    @EnumValue
    private final int code;
}