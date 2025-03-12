package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.video.VideoSubtitle;
import pers.tgl.mikufans.dto.SubtitleFile;

import java.util.List;

public interface VideoSubtitleService extends BaseService<VideoSubtitle> {
    /**
     * 获取所有关联字幕
     */
    List<VideoSubtitle> listByResId(Long resId);
    /**
     * 创建字幕,如果已存在相同语言的字幕则先删除旧的
     */
    void createSubtitle(Long vid, Long rid, SubtitleFile file);
    /**
     * 获取字幕内容
     */
    String getSubtitleText(Long id);
    /**
     * 删除字幕
     */
//    boolean deleteSubtitle(Long resId, SubtitleLanguage language);
}