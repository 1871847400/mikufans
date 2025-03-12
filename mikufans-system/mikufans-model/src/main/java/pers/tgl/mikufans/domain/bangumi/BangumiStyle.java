package pers.tgl.mikufans.domain.bangumi;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.domain.enums.VideoType;

@EqualsAndHashCode(callSuper = true)
@Data
@PermFlag(name = "节目风格", group = PermGroup.BUSINESS)
public class BangumiStyle extends SystemBaseEntity {
    /**
     * 视频类型
     */
    private VideoType videoType;
    /**
     * 风格名称
     */
    private String styleName;
}