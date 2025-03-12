package pers.tgl.mikufans.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.enums.VideoType;
import pers.tgl.mikufans.model.SearchParams;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class HistorySearch extends SearchParams {
    /**
     * 视频标题
     */
    private String title;
    /**
     * 限定视频类型
     */
    private VideoType type;
    /**
     * 限定观看开始时间
     */
    private Date begin;
    /**
     * 限定观看结束时间
     */
    private Date end;
}