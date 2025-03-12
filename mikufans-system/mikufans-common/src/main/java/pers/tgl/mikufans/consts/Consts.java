package pers.tgl.mikufans.consts;

import java.util.Arrays;
import java.util.List;

public interface Consts {
    /**
     * 每个视频下最大分集数量
     */
    int VIDEO_PART_MAX_COUNT = 200;
    /**
     * 视频标题最短长度
     */
    int VIDEO_TITLE_MIN_LENGTH = 1;
    /**
     * 视频标题最大长度
     */
    int VIDEO_TITLE_MAX_LENGTH = 64;
    /**
     * 视频简介最大长度
     */
    int VIDEO_INTRO_MAX_LENGTH = 2048;
    /**
     * 视频标签最大个数
     */
    int VIDEO_TAG_MAX_COUNT = 10;
    /**
     * 视频标签最大长度
     */
    int VIDEO_TAG_MAX_LENGTH = 16;
    /**
     * 用户最低等级
     */
    int USER_MIN_LEVEL = 0;
    /**
     * 用户最高等级
     */
    int USER_MAX_LEVEL = 6;
    /**
     * 字幕文件格式
     */
    List<String> subtitleSuffix = Arrays.asList(".vtt", ".srt", ".ass", ".sub");
}