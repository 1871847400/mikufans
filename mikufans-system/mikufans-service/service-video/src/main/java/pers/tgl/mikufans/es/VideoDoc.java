package pers.tgl.mikufans.es;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import pers.tgl.mikufans.domain.enums.VideoType;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.model.BaseDoc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * spring会自动在_source里添加 _class 属性来标注java类型
 *
 * 只存储ES检索时需要用到的字段
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(indexName = "video", writeTypeHint = WriteTypeHint.FALSE, dynamic = Dynamic.TRUE)
@FieldNameConstants
@ElasticAutoSync(tableClass = Video.class, fixedDelay = 14, joins = @ElasticAutoSync.Join(joinTable = UserDynamic.class, joinField = UserDynamic.Fields.targetId))
public class VideoDoc extends BaseDoc {
    @Field(type = FieldType.Keyword, copyTo = "all")
    private String sid;
    @Field(type = FieldType.Text, copyTo = "all", analyzer = "text_analyzer", searchAnalyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.Keyword, copyTo = "all")
    private List<String> tags;
    @Field(type = FieldType.Integer)
    private Integer duration;
    //注意：枚举存储的名称,查也需要用名称
    @Field(type = FieldType.Keyword)
    private VideoType type;
    @Field(type = FieldType.Long)
    private Long channelId;
    @Field(type = FieldType.Integer)
    private Integer search;
    @Field(type = FieldType.Double)
    private BigDecimal score;
    @Field(type = FieldType.Integer)
    private Integer plays;
    @Field(type = FieldType.Integer)
    private Integer coins;
    @Field(type = FieldType.Integer)
    private Integer stars;
    @Field(type = FieldType.Integer)
    private Integer danmus;

    @Field(type = FieldType.Date, format = {}, pattern = PATTERN)
    private Date publishTime;
    @Field(type = FieldType.Integer)
    private Integer visible;
    @Field(type = FieldType.Integer)
    private Integer disabled;

    @CompletionField(analyzer = "completion_analyzer", searchAnalyzer = "simple")
    private Completion titleCompletion;

    /**
     * copy_to字段必须指定分词器，否则会使用默认的
     * analyzer和searchAnalyzer使用的"中文"分词器必须相同,否则高亮匹配度更低
     */
    @Field(type = FieldType.Text, analyzer = "text_analyzer", searchAnalyzer = "ik_max_word")
    private String all;

    public VideoDoc() {}

    public VideoDoc(Video video, UserDynamic userDynamic) {
        BeanUtil.copyProperties(video, this);
        this.publishTime = userDynamic.getPublishTime();
        this.visible = userDynamic.getVisible();
        if (Objects.equals(disabled, 0)) {
            this.disabled = userDynamic.getDisabled();
        }
        this.titleCompletion = new Completion(Collections.singletonList(video.getTitle()));
    }
}