package pers.tgl.mikufans.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.domain.bangumi.BangumiStyle;
import pers.tgl.mikufans.domain.enums.VideoType;
import pers.tgl.mikufans.domain.system.SysRegion;
import pers.tgl.mikufans.model.Option;
import pers.tgl.mikufans.params.BangumiParams;
import pers.tgl.mikufans.service.BangumiStyleService;
import pers.tgl.mikufans.vo.BangumiIndexes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bangumi")
@RequiredArgsConstructor
public class BangumiIndexController extends BaseController {
    private final BangumiStyleService bangumiStyleService;

    /**
     * 获取索引列表
     */
    @GetMapping("/indexes")
    @Cacheable(value = "bangumi:indexes#10m", key = "#type.name()")
    public BangumiIndexes indexes(@RequestParam VideoType type) {
        BangumiIndexes result = new BangumiIndexes();
        List<Option> sorts = new ArrayList<>();
        sorts.add(new Option(type==VideoType.ANIME?"追番人数":"追剧人数", BangumiParams.Sorts.subscribe.name()));
        sorts.add(new Option("评分高低", BangumiParams.Sorts.rate.name()));
        sorts.add(new Option("开播时间", BangumiParams.Sorts.premiere.name()));
        sorts.add(new Option("播放次数", BangumiParams.Sorts.play.name()));
        result.setSorts(sorts);
        List<BangumiIndexes.Filter> filters = Collections.emptyList();
        BangumiIndexes.Filter filter1 = new BangumiIndexes.Filter("类型", "type");
        filter1.addOption("番剧", VideoType.ANIME.name());
        filter1.addOption("电影", VideoType.MOVIE.name());
        BangumiIndexes.Filter filter2 = new BangumiIndexes.Filter("地区", "region");
        List<Option> regions = Db.list(SysRegion.class)
                .stream()
                .map(a -> new Option(a.getRegionName(), a.getId() + ""))
                .collect(Collectors.toList());
        regions.add(0, new Option("全部", ""));
        filter2.setOptions(regions);
        BangumiIndexes.Filter filter3 = new BangumiIndexes.Filter("状态", "status");
        filter3.addOption("全部", "");
        filter3.addOption("连载", "1");
        filter3.addOption("完结", "2");
        BangumiIndexes.Filter filter4 = new BangumiIndexes.Filter("年份", "year");
        filter4.addOption("全部", "");
        for (int i = 0; i < 18; i++) {
            String year = DateUtil.thisYear() - i + "";
            filter4.addOption(year, year);
        }
        BangumiIndexes.Filter filter5 = new BangumiIndexes.Filter("季度", "season");
        filter5.addOption("全部", "");
        filter5.addOption("1月", "1");
        filter5.addOption("4月", "2");
        filter5.addOption("7月", "3");
        filter5.addOption("10月", "4");
        BangumiIndexes.Filter filter6 = new BangumiIndexes.Filter("风格", "style");
        List<Option> styles = bangumiStyleService.listBy(BangumiStyle::getVideoType, type)
                .stream()
                .map(a -> new Option(a.getStyleName(), a.getId() + ""))
                .collect(Collectors.toList());
        styles.add(0, new Option("全部", ""));
        filter6.setOptions(styles);
        switch (type) {
            case ANIME:
                filters = Arrays.asList(filter1, filter2, filter3, filter4, filter5, filter6);
                break;
            case MOVIE:
                filters = Arrays.asList(filter1, filter2, filter4, filter6);
                break;
        }
        result.setFilters(filters);
        return result;
    }

    @GetMapping("/anime/fastlinks")
    @Cacheable(value = "bangumi:anime_fastlinks#10m")
    public List<BangumiIndexes.Filter> fastLinks() {
        BangumiIndexes.Filter filter = new BangumiIndexes.Filter("番剧索引", "sort");
        filter.addOption("追番人数", BangumiParams.Sorts.subscribe.name());
        filter.addOption("评分高低", BangumiParams.Sorts.rate.name());
        filter.addOption("开播时间", BangumiParams.Sorts.premiere.name());
        filter.addOption("播放次数", BangumiParams.Sorts.play.name());
        BangumiIndexes.Filter filter2 = new BangumiIndexes.Filter("类型风格", "style");
        List<Option> styles = bangumiStyleService
                .listBy(BangumiStyle::getVideoType, VideoType.ANIME)
                .stream()
                .limit(8)
                .map(a -> new Option(a.getStyleName(), a.getId() + ""))
                .collect(Collectors.toList());
        filter2.setOptions(styles);
        BangumiIndexes.Filter filter3 = new BangumiIndexes.Filter("首播时间", "year");
        for (int i = 0; i < 8; i++) {
            String year = DateUtil.thisYear() - i + "";
            filter3.addOption(year, year);
        }
        BangumiIndexes.Filter filter4 = new BangumiIndexes.Filter("播放季度", "season");
        filter4.addOption("1月", "1");
        filter4.addOption("4月", "2");
        filter4.addOption("7月", "3");
        filter4.addOption("10月", "4");
        return Arrays.asList(filter, filter2, filter3, filter4);
    }
}