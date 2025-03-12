package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CarouselPosition {
    HOME(1),
    ANIME(2),
    MOVIE(3),
    CHANNEL(4);

    @EnumValue
    private final int code;
}