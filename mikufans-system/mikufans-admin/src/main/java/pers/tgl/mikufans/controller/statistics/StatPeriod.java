package pers.tgl.mikufans.controller.statistics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 统计周期
 */
@RequiredArgsConstructor
@Getter
public enum StatPeriod {
    DAY("DATE(%s)"),
    WEEK("YEARWEEK(%s)"),
    MONTH("CONCAT(YEAR(%s), MONTH(%s))"),
    YEAR("YEAR(%s)"),
    TOTAL("");

    private final String method;
}