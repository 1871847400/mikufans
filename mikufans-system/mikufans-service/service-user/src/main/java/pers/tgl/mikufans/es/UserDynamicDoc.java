package pers.tgl.mikufans.es;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.*;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.model.BaseDoc;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(indexName = "user_dynamic", writeTypeHint = WriteTypeHint.FALSE, dynamic = Dynamic.TRUE)
@FieldNameConstants
public class UserDynamicDoc extends BaseDoc {
    @Field(type = FieldType.Keyword)
    private BusinessType dynamicType;
    /**
     * 专门用于检索的内容,来源于其他业务的字段
     */
    @Field(type = FieldType.Text, analyzer = "text_analyzer", searchAnalyzer = "ik_max_word")
    private List<String> content;
    @Field(type = FieldType.Long)
    private Long shareId;
    @Field(type = FieldType.Integer)
    private Integer visible;
    @Field(type = FieldType.Integer)
    private Integer disabled;
    @Field(type = FieldType.Integer)
    private Integer publishFlag;
    @Field(type = FieldType.Date, format = {}, pattern = PATTERN)
    private Date publishTime;

    public UserDynamicDoc() {}

    public UserDynamicDoc(UserDynamic userDynamic, String ...content) {
        BeanUtil.copyProperties(userDynamic, this);
        this.content = new ArrayList<>(content.length);
        for (String str : content) {
            if (StrUtil.isNotBlank(str)) {
                this.content.add(str);
            }
        }
    }
}