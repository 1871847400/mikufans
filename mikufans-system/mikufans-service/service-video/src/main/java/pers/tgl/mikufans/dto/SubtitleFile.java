package pers.tgl.mikufans.dto;

import lombok.Data;
import pers.tgl.mikufans.domain.enums.SubtitleType;
import pers.tgl.mikufans.domain.system.SysRegion;
import pers.tgl.mikufans.validator.db.DBExists;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SubtitleFile implements Serializable {
    /**
     * 原始文本
     */
    @NotBlank
    private String data;
    /**
     * 字幕类型
     */
    @NotNull
    private SubtitleType type;
    /**
     * 字幕地区语言
     */
    @NotNull
    @DBExists(value = SysRegion.class)
    private Long regionId;
    /**
     * 原始文件名称
     */
    @NotBlank
    private String filename;
}