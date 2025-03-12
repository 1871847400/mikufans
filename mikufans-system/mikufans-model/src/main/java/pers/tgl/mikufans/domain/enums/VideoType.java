package pers.tgl.mikufans.domain.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum VideoType {
    VIDEO(1, 3, "视频"),
    ANIME(2, 1, "番剧"),
    MOVIE(3, 2, "电影");

    @EnumValue
    private final int code;
    private final int sort; //搜索页显示顺序
    private final String tag;

    public boolean isBangumi() {
        return this == ANIME || this == MOVIE;
    }

    public static List<VideoType> getSorted() {
        return Arrays.stream(values())
                .sorted(Comparator.comparingInt(VideoType::getSort))
                .collect(Collectors.toList());
    }
}