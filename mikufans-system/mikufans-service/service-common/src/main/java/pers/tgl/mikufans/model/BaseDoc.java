package pers.tgl.mikufans.model;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.Serializable;
import java.util.Date;

/**
 * ES文档基类
 * 建议：
 *  1.将多张表的关联数据拉平后存入ES,便于索引
 *  2.尽量只存储ES检索时需要用到的字段,其它字段用mysql补充
 */
@Data
@FieldNameConstants
@Setting(settingPath = "es/index_setting.json")
public class BaseDoc implements Serializable {
    /**
     * 必须按ES支持的时间格式写,如果格式不正确则会将日期存为TEXT类型
     */
    public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss'+08:00'";

    /**
     * 注解id后会自动和_id相对应，也就是和es的文档id相同
     */
    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Date, format = {}, pattern = PATTERN)
    private Date createTime;
}