package pers.tgl.mikufans.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.domain.common.SearchHistory;
import pers.tgl.mikufans.model.BaseSearch;
import pers.tgl.mikufans.service.SearchHistoryService;

import java.util.List;

/**
 * 搜索记录
 */
@RestController
@RequestMapping("/search/history")
@RequiredArgsConstructor
public class SearchHistoryController extends BaseController {
    private final SearchHistoryService searchHistoryService;
    /**
     * 查找搜索记录
     */
    @GetMapping
    public IPage<SearchHistory> search(BaseSearch<SearchHistory> params) {
        return searchHistoryService.search(params);
    }
    /**
     * 添加搜索记录
     */
    @AppLog("添加搜索记录")
    @PostMapping
    public void create(@RequestParam String content) {
        searchHistoryService.createOne(content);
    }
    /**
     * 删除搜索记录
     */
    @AppLog("删除搜索记录")
    @DeleteMapping
    public void remove(@RequestParam String content) {
        searchHistoryService.removeOne(content);
    }
    /**
     * 删除全部搜索记录
     */
    @AppLog("删除全部搜索记录")
    @DeleteMapping("/all")
    public void removeAll() {
        searchHistoryService.removeAll();
    }
    /**
     * 获取过去一个月(30天)的热搜
     */
    @GetMapping("/hotkeys")
    public List<String> getHotkeys() {
        return searchHistoryService.getHotKeys(DateUtil.lastMonth(), null, 10);
    }
}