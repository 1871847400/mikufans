package pers.tgl.mikufans.es;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.*;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.model.BaseDoc;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(indexName = "user", writeTypeHint = WriteTypeHint.FALSE)
@Setting(settingPath = "es/index_setting.json")
@NoArgsConstructor
@FieldNameConstants
@ElasticAutoSync(tableClass = User.class, fixedDelay = 14)
public class UserDoc extends BaseDoc {
    @Field(type = FieldType.Text, analyzer = "text_analyzer", searchAnalyzer = "ik_max_word")
    private String username;
    @Field(type = FieldType.Text, analyzer = "text_analyzer", searchAnalyzer = "ik_max_word")
    private String nickname;
    @Field(type = FieldType.Keyword, index = false)
    private String sign;
    @Field(type = FieldType.Long, index = false)
    private Long avatarId;
    @Field(type = FieldType.Integer)
    private Integer level;
    @Field(type = FieldType.Integer)
    private Integer exp;
    @Field(type = FieldType.Integer)
    private Integer fans;
    @Field(type = FieldType.Integer)
    private Integer follows;
    @Field(type = FieldType.Integer)
    private Integer videos;
    @Field(type = FieldType.Integer)
    private Integer articles;
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registerTime;

    public UserDoc(User user) {
        BeanUtil.copyProperties(user, this);
        this.setUserId(user.getId());
    }
}