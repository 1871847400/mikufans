package pers.tgl.mikufans.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.consts.VideoStatus;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.enums.UserFlags;
import pers.tgl.mikufans.domain.system.SysAudit;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.domain.user.UserLike;
import pers.tgl.mikufans.domain.user.UserLikeData;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoChannel;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.domain.video.VideoProcess;
import pers.tgl.mikufans.es.VideoDoc;
import pers.tgl.mikufans.event.SearchEvent;
import pers.tgl.mikufans.mapper.VideoMapper;
import pers.tgl.mikufans.model.BaseDoc;
import pers.tgl.mikufans.params.VideoParams;
import pers.tgl.mikufans.params.VideoUploadParams;
import pers.tgl.mikufans.search.SearchDateRange;
import pers.tgl.mikufans.search.VideoDurationRange;
import pers.tgl.mikufans.search.VideoSearchSort;
import pers.tgl.mikufans.service.*;
import pers.tgl.mikufans.util.DbUtils;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.vo.BangumiVo;
import pers.tgl.mikufans.vo.HighlightText;
import pers.tgl.mikufans.vo.VideoVo;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoSearchServiceImpl extends BaseServiceImpl<Video, VideoMapper> implements VideoSearchService {
    private final BangumiService bangumiService;
    private final UserFollowService userFollowService;
    private final ElasticsearchOperations elasticsearchOperations;
    private final UserFlagService userFlagService;
    private final VideoWatchHistoryService videoWatchHistoryService;
    private final VideoPartService videoPartService;
    private final UserDynamicService userDynamicService;
    private final VideoChannelService videoChannelService;

    @Override
    public PageImpl<VideoVo> search(VideoParams params) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(VideoDoc.Fields.search, 1))
                .filter(QueryBuilders.termQuery(VideoDoc.Fields.visible, 1))
                .filter(QueryBuilders.termQuery(VideoDoc.Fields.disabled, 0));
        List<HighlightBuilder.Field> highlightFields = new ArrayList<>(1);
        String kw = params.getKeyword();
        if (StrUtil.isNotBlank(kw)) {
            // 使用matchQuery会将关键词分词后查询，从而因为sort导致无关的在前面
            boolQuery.filter(QueryBuilders.matchQuery(VideoDoc.Fields.all, kw));
            //只在标题中高亮搜索内容
            highlightFields.add(new HighlightBuilder.Field(VideoDoc.Fields.title)
                    .requireFieldMatch(false)
                    .preTags("<em>")
                    .postTags("</em>"));
        }
        if (params.getType() != null) {
            boolQuery.filter(QueryBuilders.termQuery(VideoDoc.Fields.type, params.getType()));
        }
        if (params.getUserId() != null) {
            boolQuery.filter(QueryBuilders.termQuery(BaseDoc.Fields.userId, params.getUserId()));
            if (userFlagService.isDeny(params.getUserId(), UserFlags.PUBLIC_UPLOAD)) {
                return PageImpl.empty();
            }
        }
        if (StrUtil.isNotBlank(params.getTag())) {
            boolQuery.filter(QueryBuilders.termsQuery(VideoDoc.Fields.tags, params.getTag()));
        }
        if (BooleanUtil.isTrue(params.getSearchFollow())) {
            boolQuery.filter(QueryBuilders.termsQuery(BaseDoc.Fields.userId, userFollowService.getFollowIds(null)));
        }
        if (StrUtil.isNotBlank(params.getDate())) {
            SearchDateRange dateRange = EnumUtil.fromStringQuietly(SearchDateRange.class, params.getDate());
            String startDate = null;
            String endDate = null;
            if (dateRange == null) {
                String[] ranges = params.getDate().split("_");
                startDate = ranges[0];
                endDate = ranges[1];
            } else {
                startDate = DateUtil.offset(new Date(), DateField.DAY_OF_YEAR, dateRange.getOffset()).toDateStr();
            }
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(VideoDoc.Fields.publishTime)
                    .format("yyyy-MM-dd")
                    .timeZone("+08:00");
            if (startDate != null) {
                rangeQueryBuilder.gte(startDate);
            }
            if (endDate != null) {
                rangeQueryBuilder.lte(endDate);
            }
            if (startDate != null || endDate != null) {
                boolQuery.must(rangeQueryBuilder);
            }
        }
        if (isValidId(params.getChannelId())) {
            VideoChannel channel = videoChannelService.getOneWithChildren(params.getChannelId());
            if (channel != null) {
                List<Long> channelIds = new ArrayList<>();
                channelIds.add(channel.getId());
                for (VideoChannel child : channel.getChildren()) {
                    channelIds.add(child.getId());
                }
                boolQuery.filter(QueryBuilders.termsQuery(VideoDoc.Fields.channelId, channelIds));
            }
        }
        if (params.getDuration() != VideoDurationRange.ALL) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(VideoDoc.Fields.duration)
                    .gte(params.getDuration().getStartMs());
            if (params.getDuration().getEndMs() == -1) {
                rangeQueryBuilder.lte(params.getDuration().getEndMs());
            }
            boolQuery.filter(rangeQueryBuilder);
        }
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withHighlightFields(highlightFields)
                .withSort(Sort.by(Sort.Direction.DESC, params.getSort().getProperty()))
                .withPageable(params.pageRequest())
                .build();
        if (params.getPageSize() == 0) {
            long count = elasticsearchOperations.count(query, VideoDoc.class);
            return params.page(Collections.emptyList(), count);
        }
        SearchHits<VideoDoc> hits = elasticsearchOperations.search(query, VideoDoc.class);
        List<VideoVo> result = new ArrayList<>((int) hits.getTotalHits());
        for (SearchHit<VideoDoc> searchHit : hits.getSearchHits()) {
            Long id = searchHit.getContent().getId();
            VideoVo video = getById(id, VideoVo.class);
            if (video != null) {
                //追加高亮字段
                List<String> list = searchHit.getHighlightField(VideoDoc.Fields.title);
                if (CollUtil.isNotEmpty(list)) {
                    video.setHighlightTitle(list.get(0));
                }
                video.setBangumi(bangumiService.getVoById(video.getBangumiId()));
                video.setHistory(videoWatchHistoryService.getVoByVid(video.getId()));
                video.setCanplayCount(videoPartService.countCanplay(id, true));
                video.setDynamic(userDynamicService.getVoById(id));
                result.add(video);
            }
        }
        publishEvent(new SearchEvent(this, SearchEvent.SearchType.VIDEO, params.getKeyword()));
        return params.page(result, hits.getTotalHits());
    }

    @Override
    public PageImpl<Video> getLikeVideos(Long userId) {
        return wrapper()
                .selectAll(Video.class)
                .innerJoin(UserDynamic.class, UserDynamic::getTargetId, Video::getId)
                .innerJoin(UserLikeData.class, UserLikeData::getBusiId, Video::getId)
                .innerJoin(UserLike.class, UserLike::getLikeDataId, UserLikeData::getId)
                .eq(UserLike::getUserId, userId)
                .eq(UserDynamic::getVisible, 1)
                .eq(Video::getSearch, 1)
                .eq(Video::getDisabled, 0)
                .page(BaseController.createPage());
    }

    @Override
    public PageImpl<VideoVo> getUploadList(VideoUploadParams params) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        MPJLambdaWrapper<Video> wrapper = wrapper()
                .distinct()
                .selectAll()
                .eq(Video::getUserId, contextUserId)
                .eqIfExists(Video::getType, params.getVideoType())
                .leftJoin(VideoPart.class, VideoPart::getVideoId, Video::getId)
                .leftJoin(VideoProcess.class, VideoProcess::getPartId, VideoPart::getId)
                .leftJoin(SysAudit.class, SysAudit::getTargetId, VideoPart::getId);
        if (params.getStatus() == VideoStatus.DOING) {
            wrapper.eq(Video::getDisabled, 0).and(w -> {
                w.eq(SysAudit::getAuditStatus, AuditStatus.UNKNOWN).or()
                        .eq(VideoProcess::getResult, 0);
            });
        } else if (params.getStatus() == VideoStatus.SUCCESS) {
            wrapper.eq(Video::getDisabled, 0)
                    .eq(VideoPart::getCanplay, 1)
                    .groupBy(Video::getId)
                    .having("count(*)>=(select count(*) from video_part where video_id = t.id)");
        } else if (params.getStatus() == VideoStatus.FAIL) {
            wrapper.and(w -> {
                w.eq(Video::getDisabled, 1).or()
                        .eq(VideoPart::getDisabled, 1).or()
                        .eq(SysAudit::getAuditStatus, AuditStatus.FAIL).or()
                        .eq(VideoProcess::getResult, 2);
            });
        }
        if (params.getSort() == VideoSearchSort.TIME) {
            wrapper.orderBy(true, params.isAsc(), Video::getCreateTime);
        } else {
            String col = DbUtils.getColumnName(Video.class, params.getSort().getProperty());
            wrapper.orderBy(true, params.isAsc(), col);
        }
        return wrapper.page(params.page(), VideoVo.class).fill(vo -> {
            vo.setBangumi(bangumiService.getVoById(vo.getBangumiId()));
            vo.setStatusCountMap(videoPartService.countAuditStatus(vo.getId()));
            vo.setDynamic(userDynamicService.getVoById(vo.getId()));
        });
    }

    @Override
    public List<HighlightText> getCompletions(String prefix, int size) {
        if (StrUtil.isBlank(prefix)) {
            return Collections.emptyList();
        }
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        CompletionSuggestionBuilder suggestionBuilder = SuggestBuilders
                .completionSuggestion(VideoDoc.Fields.titleCompletion)
                .prefix(prefix)
                .skipDuplicates(true)
                .size(size);
        queryBuilder.withSuggestBuilder(new SuggestBuilder().addSuggestion("test", suggestionBuilder));
        SearchHits<VideoDoc> searchHits = elasticsearchOperations.search(queryBuilder.build(), VideoDoc.class);
        if (!searchHits.hasSuggest()) {
            return Collections.emptyList();
        }
        List<HighlightText> result = new ArrayList<>(size);
        try {
            List<? extends Suggest.Suggestion.Entry.Option> options = searchHits.getSuggest().getSuggestions().get(0).getEntries().get(0).getOptions();
            for (Suggest.Suggestion.Entry.Option option : options) {
                String text = option.getText();
                //如果搜索词和匹配结果相同则跳过
                if (text.equalsIgnoreCase(prefix)) {
                    continue;
                }
                //此处高亮不需要分词高亮,高亮搜索词就行了
                String highlighted = text.replaceAll(prefix, "<em>" + prefix + "</em>");
                result.add(new HighlightText(text, highlighted));
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    @Override
    public List<VideoVo> getRecommend(int size) {
        return wrapper()
                .selectAll()
                .selectAssociation(UserDynamic.class, VideoVo::getDynamic)
                .innerJoin(UserDynamic.class, UserDynamic::getTargetId, Video::getId)
                .eq(Video::getSearch, 1)
                .eq(Video::getDisabled, 0)
                .eq(UserDynamic::getVisible, 1)
                .eq(UserDynamic::getDisabled, 0)
                .eq(UserDynamic::getPublishFlag, 0)
                .orderBy(true, false, "rand()")
                .last("limit " + size)
                .list(VideoVo.class);
    }

    @Override
    public List<VideoVo> getRelateList(Long videoId, int size) {
        Video video = getById(videoId);
        if (video == null) {
            return Collections.emptyList();
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .mustNot(QueryBuilders.termQuery(BaseDoc.Fields.id, videoId))
                .filter(QueryBuilders.termQuery(VideoDoc.Fields.type, video.getType()))
                .filter(QueryBuilders.termQuery(VideoDoc.Fields.search, 1))
                .filter(QueryBuilders.termQuery(VideoDoc.Fields.visible, 1))
                .filter(QueryBuilders.termQuery(VideoDoc.Fields.disabled, 0));
        FunctionScoreQueryBuilder.FilterFunctionBuilder filter1 = new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                QueryBuilders.matchQuery(VideoDoc.Fields.title, video.getTitle()),
                ScoreFunctionBuilders.weightFactorFunction(10)
        );
//        FunctionScoreQueryBuilder.FilterFunctionBuilder filter2 = new FunctionScoreQueryBuilder.FilterFunctionBuilder(
//                QueryBuilders.matchQuery(VideoDoc.Fields., video.getType()),
//                ScoreFunctionBuilders.weightFactorFunction(50)
//        );
        FunctionScoreQueryBuilder.FilterFunctionBuilder filter3 = new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                QueryBuilders.matchQuery(VideoDoc.Fields.channelId, video.getChannelId()),
                ScoreFunctionBuilders.weightFactorFunction(5)
        );
        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filters = {filter1, filter3};
        FunctionScoreQueryBuilder builder = QueryBuilders.functionScoreQuery(boolQueryBuilder, filters)
                .boostMode(CombineFunction.SUM);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
//                .withQuery(boolQueryBuilder)
                .withQuery(builder)
                .withPageable(Pageable.ofSize(size))
                .build();
        SearchHits<VideoDoc> searchHits = elasticsearchOperations.search(query, VideoDoc.class);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .map(doc -> {
                    VideoVo vo = getById(doc.getId(), VideoVo.class);
                    if (vo != null) {
                        vo.setBangumi(bangumiService.getById(vo.getBangumiId(), BangumiVo.class));
                    }
                    return vo;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}