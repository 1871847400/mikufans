package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.article.Article;
import pers.tgl.mikufans.domain.user.UserPublish;
import pers.tgl.mikufans.domain.user.UserRate;
import pers.tgl.mikufans.domain.video.Video;

@RequiredArgsConstructor
@Getter
@Deprecated
public enum CommentTarget {
    VIDEO(1, "视频", Video.class),
    PUBLISH(2, "说说", UserPublish.class),
    RATE(3, "点评", UserRate.class),
    ARTICLE(4, "文章", Article.class);

    @EnumValue
    private final int code;

    private final String label;

    private final Class<? extends UserBaseEntity> modelClass;
}