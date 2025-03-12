package pers.tgl.mikufans.domain.video;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.util.MyUtils;

import java.util.Date;

/**
 * 视频观看历史表
 * @TableName video_watch_history
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="video_watch_history")
@FieldNameConstants
@Data
public class VideoWatchHistory extends UserBaseEntity {
    /**
     * 视频id
     */
    private Long videoId;
    /**
     * 分集id
     */
    private Long partId;
    /**
     * 上次观看位置(ms)
     */
    private Integer watchPos;
    /**
     * 观看总时长(ms)
     */
    private Integer watchTime;
    /**
     * 观看设备 0未知 1PC 2APP
     */
    private Integer device;
    /**
     * 上次观看时间
     */
    public Date getLastWatchTime() {
        return getUpdateTime();
    }
    /**
     * 最近一次观看日期
     */
    public String getLastWatchTimeStr() {
        return MyUtils.formatHumanTime(getUpdateTime());
    }
}