package pers.tgl.mikufans.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
@Getter
public enum VideoDurationRange {
    ALL("全部时长", 0, 0),
    RANGE_1("10分钟以下", 0, 10),
    RANGE_2("10-30分钟", 10, 30),
    RANGE_3("30-60分钟", 30, 60),
    RANGE_4("60分钟以上", 60, -1);

    private final String label;
    private final int start;
    private final int end;

    public long getStartMs() {
        return Duration.ofMinutes(start).toMillis();
    }

    public long getEndMs() {
        return Duration.ofMinutes(end).toMillis();
    }
}