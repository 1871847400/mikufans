package pers.tgl.mikufans.domain.video;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.enums.SubtitleType;
import pers.tgl.mikufans.domain.system.SysRegion;

/**
 * 视频字幕表
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoSubtitle extends UserBaseEntity {
    /**
     * 视频id
     */
    private Long vid;
    /**
     * 资源id
     */
    private Long rid;
    /**
     * 字幕类型
     */
    private SubtitleType type;
    /**
     * 字幕语言
     */
    private Long regionId;
    /**
     * 原始文件名称(用户上传时)
     */
    private String filename;
    /**
     * 新文件名(存储后使用的名字)
     */
    private String saveName;

    @TableField(exist = false)
    private SysRegion region;
}