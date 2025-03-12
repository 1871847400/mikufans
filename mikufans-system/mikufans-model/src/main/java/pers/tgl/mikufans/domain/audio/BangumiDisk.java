package pers.tgl.mikufans.domain.audio;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

import java.util.List;

@Deprecated
@EqualsAndHashCode(callSuper = true)
@Data
public class BangumiDisk extends UserBaseEntity {
    /**
     * 节目id
     */
    private Long bangumiId;
    /**
     * 光盘名称
     */
    private String diskName;
    /**
     * 声音资源id
     */
    private Long resId;
    /**
     * 光盘图片id列表
     */
    private List<String> coverIds;
}