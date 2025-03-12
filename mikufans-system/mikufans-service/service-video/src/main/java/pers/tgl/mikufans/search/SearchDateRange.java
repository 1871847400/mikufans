package pers.tgl.mikufans.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchDateRange {
    DAY("最近一天", -1),
    WEEK("最近一周", -7),
    MONTH("最近一月", -30),
    HALF_YEAR("最近半年", -365/2),
    CUSTOM("", 0);

    private final String label;
    private final int offset;
}