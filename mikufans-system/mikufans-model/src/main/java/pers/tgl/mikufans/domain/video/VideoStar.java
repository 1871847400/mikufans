package pers.tgl.mikufans.domain.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

import java.util.Date;

/**
 * 视频收藏记录表
 * @TableName video_star
 */
@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
public class VideoStar extends UserBaseEntity {
    /**
     * 视频id
     */
    private Long videoId;
    /**
     * 收藏夹id
     */
    private Long starId;
    /**
     * 收藏时间,只显示年月日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getStarDate() {
        return getCreateTime();
    }
}