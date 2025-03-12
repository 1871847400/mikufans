package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Deprecated
@AllArgsConstructor
@Getter
public enum ReadFlag {
    /**
     * 0未读
     */
    NO_READ(0),
    /**
     * 1已读
     */
    READ(1);

    @EnumValue
    private final int value;
}