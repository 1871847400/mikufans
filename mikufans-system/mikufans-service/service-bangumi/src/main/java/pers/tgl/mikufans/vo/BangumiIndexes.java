package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import pers.tgl.mikufans.model.Option;

import java.util.ArrayList;
import java.util.List;

@Data
public class BangumiIndexes {
    /**
     * 排序的按钮列表 名称 ：query value
     */
    private List<Option> sorts;
    /**
     * 筛选列表
     */
//    private List<Option> types;
    private List<Filter> filters;

    @Data
    @NoArgsConstructor
    public static class Filter {
        private String label; //筛选名称，如风格，年份
        private String query; //query key
        private List<Option> options; //选项列表
        public Filter(String label, String query) {
            this.label = label;
            this.query = query;
            this.options = new ArrayList<>();
        }
        public void addOption(String label, String query) {
            this.options.add(new Option(label, query));
        }
    }
}