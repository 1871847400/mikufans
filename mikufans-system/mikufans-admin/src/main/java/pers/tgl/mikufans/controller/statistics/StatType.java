package pers.tgl.mikufans.controller.statistics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pers.tgl.mikufans.domain.article.Article;
import pers.tgl.mikufans.domain.base.BaseEntity;
import pers.tgl.mikufans.domain.comment.UserComment;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.domain.user.UserPublish;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoDanmu;

/**
 * 统计类型
 */
@RequiredArgsConstructor
@Getter
public enum StatType {
    /**
     * 用户注册
     */
    USER_REGISTER(User.class, "累计用户", "icon-zhanghao"),
    /**
     * 视频投稿
     */
    VIDEO_UPLOAD(Video.class, "累计视频", "icon-shipin4"),
    /**
     * 文章投稿
     */
    ARTICLE_UPLOAD(Article.class, "累计文章", "icon-wenben"),
    /**
     * 说说发布
     */
    PUBLISH_SEND(UserPublish.class, "累计说说", "icon-pinglun2"),
    /**
     * 弹幕发送
     */
    DANMU_SEND(VideoDanmu.class, "累计弹幕", "icon-bofangqi-danmudingbukai"),
    /**
     * 评论发送
     */
    COMMENT_SEND(UserComment.class, "累计评论", "icon-pinglun2");

    private final Class<? extends BaseEntity> entityClass;
    private final String title;
    private final String icon;
}