package pers.tgl.mikufans.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pers.tgl.mikufans.consts.Consts;
import pers.tgl.mikufans.domain.enums.VideoType;
import pers.tgl.mikufans.model.SearchParams;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class BangumiParams extends SearchParams {
    /**
     * 视频标题
     */
    @Length(max = Consts.VIDEO_TITLE_MAX_LENGTH)
    private String title;
    /**
     * 限制类型
     */
    private VideoType videoType;
    /**
     * 限定更新状态
     */
    @Range(min = 0, max = 2)
    private Integer status;
    /**
     * 限制年份
     */
    private Integer year;
    /**
     * 限定季度(主要用于番剧)
     */
    @Range(min = 1, max = 4)
    private Integer season;
    /**
     * 限定星期几更新
     */
    private Integer week;
    /**
     * 只返回该用户已经订阅的
     */
    private Long subscribedUserId;
    /**
     * 排序规则
     */
    @NotNull
    private Sorts sort = Sorts.random;
    /**
     * 是否升序
     */
    private boolean asc = false;
    /**
     * 限制风格
     */
    private Long style;
    /**
     * 限制地区
     */
    private Long region;

    public static enum Sorts {
        subscribe, //订阅人数
        rate, //评分高低
        premiere, //首播日期
        subscribe_time,//订阅时间
        random, //随机
        play, //播放次数
    }
}