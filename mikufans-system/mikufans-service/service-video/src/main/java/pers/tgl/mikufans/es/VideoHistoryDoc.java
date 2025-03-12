package pers.tgl.mikufans.es;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import pers.tgl.mikufans.domain.enums.VideoType;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoWatchHistory;
import pers.tgl.mikufans.model.BaseDoc;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@Document(indexName = "video_history")
@ElasticAutoSync(
        tableClass = VideoWatchHistory.class,
        joins = @ElasticAutoSync.Join(thisField = VideoWatchHistory.Fields.videoId, joinTable = Video.class)
)
public class VideoHistoryDoc extends BaseDoc {
    /**
     * 视频id
     */
    @Field(type = FieldType.Long)
    private Long videoId;
    /**
     * 分集id
     */
    @Field(type = FieldType.Long)
    private Long partId;
    /**
     * 上次观看时间
     */
    @Field(type = FieldType.Date, format = {}, pattern = PATTERN)
    private Date watchTime;
    /**
     * 视频标题
     */
    @Field(type = FieldType.Text, analyzer = "text_analyzer", searchAnalyzer = "ik_max_word")
    private String title;
    /**
     * 视频类型
     */
    @Field(type = FieldType.Keyword)
    private VideoType videoType;

    public VideoHistoryDoc() {}

    public VideoHistoryDoc(VideoWatchHistory history, Video video) {
        BeanUtil.copyProperties(history, this);
        this.watchTime = history.getLastWatchTime();
        this.title = video.getTitle();
        this.videoType = video.getType();
    }
}