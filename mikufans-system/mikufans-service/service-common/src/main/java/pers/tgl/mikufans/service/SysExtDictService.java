package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.system.SysExtDict;

import java.util.List;

public interface SysExtDictService extends BaseService<SysExtDict> {
    /**
     * 保存或更新词条
     */
    void saveDto(SysExtDict sysExtDict);
    /**
     * 将关键词过滤为指定字符
     */
    String filterSensitiveWord(String content, char replaceChar);
    /**
     * 获取所有扩展词
     */
    List<String> getWordList();
    /**
     * 上次增删改的时间
     */
    String getLastUpdateDate();
}