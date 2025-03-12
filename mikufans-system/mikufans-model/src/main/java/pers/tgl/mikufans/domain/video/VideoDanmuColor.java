package pers.tgl.mikufans.domain.video;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;

@EqualsAndHashCode(callSuper = true)
@TableName(value ="video_danmu_color")
@Data
@PermFlag(name = "弹幕颜色", group = PermGroup.BUSINESS)
public class VideoDanmuColor extends SystemBaseEntity {
    /**
     * 颜色名称(英文)
     */
    private String colorName;
    /**
     * 颜色代码 #fff
     */
    private String colorCode;
    /**
     * 是否停用
     */
    private Integer disabled;
}