package pers.tgl.mikufans.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.domain.video.VideoWatchHistory;
import pers.tgl.mikufans.dto.VideoWatchDto;
import pers.tgl.mikufans.es.VideoHistoryDoc;
import pers.tgl.mikufans.manager.VideoWatchManager;
import pers.tgl.mikufans.mapper.VideoWatchHistoryMapper;
import pers.tgl.mikufans.params.HistorySearch;
import pers.tgl.mikufans.service.VideoWatchHistoryService;
import pers.tgl.mikufans.util.MyUtils;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.util.ServletUtils;
import pers.tgl.mikufans.vo.VideoWatchHistoryVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TGL
 * @description 针对表【video_watch_history(视频观看历史表)】的数据库操作Service实现
 * @createDate 2022-12-31 10:17:31
 */
@Service
@RequiredArgsConstructor
public class VideoWatchHistoryServiceImpl extends BaseServiceImpl<VideoWatchHistory, VideoWatchHistoryMapper>
        implements VideoWatchHistoryService {
    private final VideoWatchManager videoWatchManager;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public VideoWatchHistoryVo getVoByVid(Long videoId) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (contextUserId == null) {
            return null;
        }
        VideoWatchHistoryVo vo = wrapper()
                .eq(VideoWatchHistory::getUserId, contextUserId)
                .eq(VideoWatchHistory::getVideoId, videoId)
                .one(VideoWatchHistoryVo.class);
        if (vo != null) {
            vo.setVideo(Db.getById(vo.getVideoId(), Video.class));
            vo.setPart(Db.getById(vo.getPartId(), VideoPart.class));
        }
        return vo;
    }

    @Override
    public PageImpl<VideoWatchHistoryVo> search(HistorySearch params) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (contextUserId == null) {
            return PageImpl.empty();
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (params.getType() != null) {
            boolQuery.filter(QueryBuilders.termQuery(VideoHistoryDoc.Fields.videoType, params.getType()));
        }
        List<HighlightBuilder.Field> highlightFields = new ArrayList<>(1);
        if (StrUtil.isNotBlank(params.getTitle())) {
            boolQuery.filter(QueryBuilders.matchQuery(VideoHistoryDoc.Fields.title, params.getTitle()));
            highlightFields.add(new HighlightBuilder.Field(VideoHistoryDoc.Fields.title)
                    .requireFieldMatch(false)
                    .preTags("<em>")
                    .postTags("</em>"));
        }
        if (params.getBegin() != null || params.getEnd() != null) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(VideoHistoryDoc.Fields.watchTime)
                    .format("yyyy-MM-dd HH:mm:ss")
                    .timeZone("+08:00");
            if (params.getBegin() != null) {
                rangeQuery.gte(params.getBegin());
            }
            if (params.getEnd() != null) {
                rangeQuery.lte(params.getEnd());
            }
            boolQuery.filter(rangeQuery);
        }
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withHighlightFields(highlightFields)
                .withSort(Sort.by(Sort.Direction.DESC, VideoHistoryDoc.Fields.watchTime))
                .withPageable(params.pageRequest())
                .build();
        SearchHits<VideoHistoryDoc> hits = elasticsearchOperations.search(query, VideoHistoryDoc.class);
        List<VideoWatchHistoryVo> records = new ArrayList<>(hits.getSearchHits().size());
        for (SearchHit<VideoHistoryDoc> searchHit : hits.getSearchHits()) {
            List<String> list = searchHit.getHighlightField(VideoHistoryDoc.Fields.title);
            VideoWatchHistoryVo vo = getVoByVid(searchHit.getContent().getVideoId());
            if (vo != null) {
                if (CollUtil.isNotEmpty(list)) {
                    vo.setHighlighted(list.get(0));
                }
                records.add(vo);
            }
        }
        return params.page(records, hits.getTotalHits());
    }

    @Override
    public void create(VideoWatchDto dto) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        int diffTime = videoWatchManager.record(dto);
        VideoWatchHistory result = lambdaQuery()
                .select(VideoWatchHistory::getId, VideoWatchHistory::getWatchTime,
                        VideoWatchHistory::getWatchPos, VideoWatchHistory::getVersion)
                .eq(VideoWatchHistory::getUserId, contextUserId)
                .eq(VideoWatchHistory::getVideoId, dto.getVideoId())
                .one();
        int device = MyUtils.getDeviceType(ServletUtils.getRequest());
        if (result == null) {
            result = new VideoWatchHistory();
            result.setVideoId(dto.getVideoId());
            result.setPartId(dto.getPartId());
            result.setUserId(contextUserId);
            result.setWatchPos(dto.getWatchPos());
            result.setWatchTime(0);
            result.setDevice(device);
            save(result);
            elasticsearchOperations.save(new VideoHistoryDoc(result, Db.getById(result.getVideoId(), Video.class)));
        } else {
            result.setPartId(dto.getPartId());
            result.setWatchPos(dto.getWatchPos());
            result.setWatchTime(result.getWatchTime() + diffTime);
            result.setDevice(device);
            updateById(result); //必须传实体才能触发字段自动填充和乐观锁
        }
    }

    @Override
    public void removeAllByUserId() {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        lambdaUpdate().eq(VideoWatchHistory::getUserId, contextUserId).remove();
    }

    @Override
    public VideoWatchHistory getCurrentHistory(Long vid) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (contextUserId == null) {
            return null;
        }
        return lambdaQuery()
                .eq(VideoWatchHistory::getUserId, contextUserId)
                .eq(VideoWatchHistory::getVideoId, vid)
                .one();
    }
}