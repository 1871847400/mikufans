package pers.tgl.mikufans.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QueryOption {
    //前端查询参数的key
    private String query;
    //默认值
    private String value;
    //单选列表
    private List<Option> options;

    public QueryOption() {
        this.options = new ArrayList<>();
    }

    public QueryOption(String query, String value) {
        this();
        this.query = query;
        this.value = value;
    }

    public void addOption(String label, Object value) {
        this.options.add(new Option(label, value));
    }
}