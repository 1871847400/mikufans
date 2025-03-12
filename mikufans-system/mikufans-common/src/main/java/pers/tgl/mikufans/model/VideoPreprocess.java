package pers.tgl.mikufans.model;

import lombok.Data;
import pers.tgl.mikufans.validator.group.Create;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
public class VideoPreprocess implements Serializable {
    /**
     * 资源分段，将视频按指定时间范围进行分成几块最后再按顺序合并
     */
    @Size(max = 5, groups = Create.class)
    private List<VideoSegment> segments;
    /**
     * 视频水印
     */
    @Valid
    private Watermark watermark;
    /**
     * 内嵌字幕内容,原始文本
     */
    private String subtitle;
}