package pers.tgl.mikufans.consts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 代表时间周期
 */
@RequiredArgsConstructor
@Getter
public enum Period {
    /**
     * 今天
     */
    DAY("DAY"),
    /**
     * 本周
     */
    WEEK("WEEK"),
    /**
     * 本月
     */
    MONTH("MONTH"),
    /**
     * 本年
     */
    YEAR("YEAR");
    /**
     * SQL函数
     */
    private final String func;
}