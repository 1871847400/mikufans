package pers.tgl.mikufans.vo;

import lombok.Data;

@Data
public class BangumiSeries {
    /**
     * bangumi id
     */
    private Long bid;
    /**
     * video id
     */
    private Long vid;
    /**
     * video sid
     */
    private String sid;
    /**
     * 标签名
     */
    private String seriesTag;
}