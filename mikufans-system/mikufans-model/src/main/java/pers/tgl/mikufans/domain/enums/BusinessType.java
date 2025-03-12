package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pers.tgl.mikufans.domain.article.Article;
import pers.tgl.mikufans.domain.bangumi.Bangumi;
import pers.tgl.mikufans.domain.comment.UserComment;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.domain.user.UserPublish;
import pers.tgl.mikufans.domain.user.UserRate;
import pers.tgl.mikufans.domain.user.UserStar;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoDanmu;
import pers.tgl.mikufans.domain.video.VideoPart;

/**
 * 全部业务类型
 */
@Getter
@RequiredArgsConstructor
public enum BusinessType {
    VIDEO(1, "视频", Video.class),
    VIDEO_PART(2, "视频", VideoPart.class),
    ANIME(3, "番剧", Bangumi.class),
    MOVIE(4, "电影", Video.class),
    DYNAMIC(5, "动态", UserDynamic.class),
    COMMENT(6, "评论", UserComment.class),
    DANMU(7, "弹幕", VideoDanmu.class),
    PUBLISH(8, "说说", UserPublish.class),
    ARTICLE(9, "文章", Article.class),
    RATE(10, "评价", UserRate.class),
    STAR(11, "收藏夹", UserStar.class);

    @EnumValue
    private final int code;
    private final String label;
    private final Class<?> model;
}