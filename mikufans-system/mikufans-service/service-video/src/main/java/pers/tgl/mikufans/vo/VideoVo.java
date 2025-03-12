package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoWatchHistory;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class VideoVo extends Video {
    /**
     * 上传用户
     */
    private UserVo user;
    /**
     * 全部分集列表
     */
    private List<VideoPartVo> parts;
    /**
     * 能播放的分集数量
     */
    private Integer canplayCount;
    /**
     * 当前用户观看此视频的历史记录(最近的一次)
     */
    private VideoWatchHistory history;
    /**
     * 当前用户已投币数量
     */
    private Integer userCoin;
    /**
     * 当前用户是否收藏
     */
    private Boolean userStar;
    /**
     * 视频标记
     */
    private Map<String, String> flags;
    /**
     * 节目信息(如果存在)
     */
    private BangumiVo bangumi;
    /**
     * 高亮后的标题
     */
    private String highlightTitle;
    /**
     * 各种状态的分集数量
     */
    private Map<AuditStatus, Integer> statusCountMap;
    /**
     * 动态数据
     */
    private UserDynamicVo dynamic;
    /**
     *
     */
    public User getUser() {
        return user != null ? user : super.getUser();
    }

    public LikeStatus getLikeStatus() {
        return dynamic != null ? dynamic.getLikeStatus() : null;
    }
}