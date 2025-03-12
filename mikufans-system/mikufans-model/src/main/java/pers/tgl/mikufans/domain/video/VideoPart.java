package pers.tgl.mikufans.domain.video;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.enums.VideoPartType;

import java.util.Date;

/**
 * 视频分集表
 * @TableName video_part
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="video_part")
@Data
@FieldNameConstants
public class VideoPart extends UserBaseEntity {
    /**
     * 视频id
     */
    private Long videoId;
    /**
     * 视频短id
     */
    private String videoSid;
    /**
     * 资源id
     */
    private Long resId;
    /**
     * 分集类型
     */
    private VideoPartType partType;
    /**
     * 视频单集名称
     */
    private String partName;
    /**
     * 排序值 将直接作为分P值
     * 不要在此列上建唯一索引,不方便程序调整
     */
    private Integer sort;
    /**
     * 封面id
     */
    private Long bannerId;
    /**
     * 是否禁用 0否 1是 禁用后无法观看和被搜素到
     * todo
     */
    private Integer disabled;
    /**
     * 弹幕数量
     */
    private Integer danmus;
    /**
     * 是否为后续追加的视频 0:否 1:是
     * 影响是否发布动态
     */
    private Integer append;
    /**
     * 是否通过审核
     */
    private Integer canplay;
    /**
     * 发布时间
     */
    public Date getPublishTime() {
        return getCreateTime();
    }

    public String getUri() {
        String uri = "/video/" + getVideoSid();
        if (getSort() > 1) {
            uri += "/" + getSort();
        }
        return uri;
    }
}