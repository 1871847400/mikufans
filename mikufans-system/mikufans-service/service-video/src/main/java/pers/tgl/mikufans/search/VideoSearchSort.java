package pers.tgl.mikufans.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pers.tgl.mikufans.es.VideoDoc;

@AllArgsConstructor
@Getter
public enum VideoSearchSort {
    SCORE(VideoDoc.Fields.score, "默认排序"),
    TIME(VideoDoc.Fields.publishTime, "最新发布"),
    PLAY(VideoDoc.Fields.plays, "最多播放"),
    COIN(VideoDoc.Fields.coins, "最多投币"),
    STAR(VideoDoc.Fields.stars, "最多收藏"),
    DANMU(VideoDoc.Fields.danmus, "最多弹幕");
    /**
     * 排序字段名称
     */
    private final String property;
    /**
     * 显示名称
     */
    private final String display;
}