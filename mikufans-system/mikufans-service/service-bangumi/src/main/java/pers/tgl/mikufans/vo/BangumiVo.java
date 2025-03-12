package pers.tgl.mikufans.vo;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.bangumi.Bangumi;
import pers.tgl.mikufans.domain.bangumi.BangumiStyle;
import pers.tgl.mikufans.domain.bangumi.BangumiSubscribe;
import pers.tgl.mikufans.domain.user.UserRate;
import pers.tgl.mikufans.domain.video.Video;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BangumiVo extends Bangumi {
    /**
     * 视频
     */
    private Video video;
    /**
     * 当前用户订阅数据
     */
    private BangumiSubscribe subscribed;
    /**
     * 当前用户的点评
     */
    private UserRate userRate;
    /**
     * 关联的系列
     */
    private List<BangumiSeries> series;
    /**
     * 风格列表
     */
    private List<BangumiStyle> styles;
    /**
     * 描述更新状态, 例如：已完结共XX集
     */
    private String desc;
    /**
     * 看到xx集 23:20
     */
    private String watchInfo;
    /**
     * 这一集的观看进度0-1
     */
    private Float watchProgress;

    @Override
    public String getUri() {
        return video != null ? "/bangumi/" + video.getSid() : super.getUri();
    }
    /**
     * 风格名称列表
     */
    public String getStyleNames() {
        return styles != null ? CollUtil.join(styles, " / ", BangumiStyle::getStyleName) : "";
    }
}