package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.domain.video.VideoWatchHistory;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoWatchHistoryVo extends VideoWatchHistory {
    /**
     * 观看的视频
     */
    private Video video;
    /**
     * 观看的分集
     */
    private VideoPart part;
    /**
     * 搜索后的高亮标题,可能为null
     */
    private String highlighted;
    /**
     * 访问地址
     */
    public String getUri() {
        if (part != null) {
            return part.getUri();
        }
        if (video != null) {
            return video.getUri();
        }
        return null;
    }
}