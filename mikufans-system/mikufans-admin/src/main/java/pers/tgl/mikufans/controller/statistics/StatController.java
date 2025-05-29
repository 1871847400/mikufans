package pers.tgl.mikufans.controller.statistics;

import cn.hutool.core.util.StrUtil;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.Data;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.controller.BaseController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/stats")
public class StatController extends BaseController {
    @GetMapping("/period")
    @Cacheable(value = "stats:period#30s")
    public Map<StatType, StatValue> getStats() {
        Map<StatType, StatValue> map = new LinkedHashMap<>();
        for (StatType statType : StatType.values()) {
            StatValue stat = new StatValue();
            stat.setTitle(statType.getTitle());
            stat.setIcon(statType.getIcon());
            Map<StatPeriod, Integer> counts = new LinkedHashMap<>();
            for (StatPeriod period : StatPeriod.values()) {
                boolean condition = StrUtil.isNotBlank(period.getMethod());
                String eq = period.getMethod().replaceAll("%s", "t.create_time");
                String val = period.getMethod().replaceAll("%s", "now()");
                Long count = new MPJLambdaWrapper<>(statType.getEntityClass())
                        .eqSql(condition, eq, val)
                        .count();
                counts.put(period, count.intValue());
            }
            stat.setCounts(counts);
            map.put(statType, stat);
        }
        return map;
    }

    @GetMapping("/trend")
    public Map<String, Integer> getTrend(@RequestParam StatType type,
                                         @RequestParam String start,
                                         @RequestParam String end) {
        List<Map<String, Object>> list = new MPJLambdaWrapper<>(type.getEntityClass())
                .select("DATE(t.create_time) as date", "count(*) as count")
                .between("t.create_time", start, end)
                .groupBy("DATE(t.create_time)")
                .mapList();
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map<String, Object> e : list) {
            result.put(e.get("date").toString(), Integer.parseInt(e.get("count").toString()));
        }
        return result;
    }

    @Data
    public static class StatValue {
        private String title;
        private String icon;
        private Map<StatPeriod, Integer> counts;
    }
}