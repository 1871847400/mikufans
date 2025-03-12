package pers.tgl.mikufans.domain.video;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

/**
 * 视频标志
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="video_flag")
@Data
public class VideoFlag extends UserBaseEntity {
    /**
     * 视频id
     */
    private Long videoId;
    /**
     * 分集id,如果为0针对所有分集
     */
    private Long partId;
    /**
     * 标志名称
     */
    private String flagName;
    /**
     * 标志的值
     */
    private String flagValue;
}