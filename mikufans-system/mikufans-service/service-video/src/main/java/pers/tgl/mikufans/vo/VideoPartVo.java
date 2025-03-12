package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pers.tgl.mikufans.domain.system.SysAudit;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.domain.video.VideoResource;
import pers.tgl.mikufans.domain.video.VideoSubtitle;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class VideoPartVo extends VideoPart {
    /**
     * 关联的资源
     */
    public VideoResource resource;
    /**
     * 资源处理进度 (0-100)
     */
    private Integer progress;
    /**
     * 是否在处理中出现错误
     */
    private Boolean processFail;
    /**
     * 特殊标志
     */
    private Map<String, String> flags;
    /**
     * 字幕列表
     */
    private List<VideoSubtitle> subtitles;
    /**
     * 审核结果
     */
    private SysAudit sysAudit;
    /**
     * hls master地址
     */
    public String getMasterUrl() {
        return "/video/resource/download/" + getId() + "/master";
    }
    /**
     * 视频预览图vtt配置地址
     */
    public String getThumbnailsVttUrl() {
        return "/video/resource/download/" + getId() + "/thumbnails.vtt";
    }
    /**
     * 视频预览图地址
     */
    public String getThumbnailsImgUrl() {
        return "/video/resource/download/" + getId() + "/thumbnails.jpg";
    }
}