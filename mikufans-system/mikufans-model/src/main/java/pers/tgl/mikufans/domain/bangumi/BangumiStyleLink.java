package pers.tgl.mikufans.domain.bangumi;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class BangumiStyleLink extends UserBaseEntity {
    /**
     * 节目id
     */
    private Long bid;
    /**
     * 风格id
     */
    private Long sid;
}