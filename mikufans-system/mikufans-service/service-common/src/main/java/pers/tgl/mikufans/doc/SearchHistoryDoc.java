package pers.tgl.mikufans.doc;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@FieldNameConstants
@Document(indexName = "search_history")
//@Setting(settingPath = "es/index_setting.json") 用不到分词器
public class SearchHistoryDoc implements Serializable {
    @Id
    @Field(type = FieldType.Long, index = false)
    private Long id;
    /**
     * 搜索内容
     * 注意text类型无法聚合
     */
    @Field(type = FieldType.Keyword)
    private String content;
    /**
     * 某一天搜索过多少次
     */
    @Field(type = FieldType.Long)
    private Long searchCount;
    /**
     * 搜索日期
     * 只存储年月日，如果用Date类型，会有时区问题
     */
    @Field(type = FieldType.Keyword)
    private String date;
}