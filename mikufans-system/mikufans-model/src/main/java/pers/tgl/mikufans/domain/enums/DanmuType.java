package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DanmuType {
    ROLL(1),
    FIXED_TOP(2),
    FIXED_BOTTOM(3);

    @EnumValue
    private final int code;
}