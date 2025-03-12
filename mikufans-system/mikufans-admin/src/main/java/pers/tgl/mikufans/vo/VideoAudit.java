package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.video.Video;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@PermFlag(name = "视频审核", group = PermGroup.BUSINESS)
public class VideoAudit extends Video {
    /**
     * 需要审核的分集
     */
    private List<VideoPartVo> parts;
    /**
     * 需要审核的数量
     */
    private Integer auditCount;
}
