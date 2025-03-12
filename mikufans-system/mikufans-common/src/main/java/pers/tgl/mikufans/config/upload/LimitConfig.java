package pers.tgl.mikufans.config.upload;

import lombok.Data;

@Data
public class LimitConfig {
    /**
     * 每天上传限制
     */
    private int daily = -1;
    /**
     * 每周上传限制
     */
    private int week = -1;
    /**
     * 总共上传限制
     */
    private int total = -1;
}
