package pers.tgl.mikufans.domain.video;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
public class VideoRepresent extends UserBaseEntity {
    /**
     * 视频id
     */
    private Long videoId;
    /**
     * 排序值
     */
    private Integer sort;
    /**
     * 原因
     */
    private String reason;
}