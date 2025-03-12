package pers.tgl.mikufans.service;

import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.common.SearchHistory;
import pers.tgl.mikufans.model.BaseSearch;
import pers.tgl.mikufans.util.PageImpl;

import java.util.Date;
import java.util.List;

public interface SearchHistoryService extends BaseService<SearchHistory> {
    /**
     * 搜索用户的搜索记录
     */
    PageImpl<SearchHistory> search(BaseSearch<SearchHistory> search);
    /**
     * 添加搜索记录，如果已存在则增加搜索次数
     */
    boolean createOne(String content);
    /**
     * 删除搜索记录
     */
    void removeOne(String content);
    /**
     * 删除所有搜索记录
     */
    void removeAll();
    /**
     * 获得热词排名
     * @param fromDate 开始日期
     * @param toDate 截止日期
     * @param size 前多少名
     * @return
     */
    List<String> getHotKeys(@Nullable Date fromDate, @Nullable Date toDate, int size);
}