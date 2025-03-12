package pers.tgl.mikufans.es;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoStar;
import pers.tgl.mikufans.model.BaseDoc;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@Document(indexName = "video_star")
@ElasticAutoSync(
        tableClass = VideoStar.class,
        fixedDelay = 18,
        joins = @ElasticAutoSync.Join(thisField = VideoStar.Fields.videoId, joinTable = Video.class)
)
public class VideoStarDoc extends BaseDoc {
    /**
     * 视频id
     */
    @Field(type = FieldType.Long)
    private Long videoId;
    /**
     * 收藏夹id
     */
    @Field(type = FieldType.Long)
    private Long starId;
    /**
     * 视频标题
     */
    @Field(type = FieldType.Text, analyzer = "text_analyzer", searchAnalyzer = "ik_max_word")
    private String title;

    public VideoStarDoc() {}

    public VideoStarDoc(VideoStar videoStar, Video video) {
        BeanUtil.copyProperties(videoStar, this);
        this.title = video.getTitle();
    }
}