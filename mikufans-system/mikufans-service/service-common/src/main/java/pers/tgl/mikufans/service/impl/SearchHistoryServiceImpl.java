package pers.tgl.mikufans.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.doc.SearchHistoryDoc;
import pers.tgl.mikufans.domain.common.SearchHistory;
import pers.tgl.mikufans.mapper.SearchHistoryMapper;
import pers.tgl.mikufans.model.BaseSearch;
import pers.tgl.mikufans.service.SearchHistoryService;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchHistoryServiceImpl extends BaseServiceImpl<SearchHistory, SearchHistoryMapper>
        implements SearchHistoryService {
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public PageImpl<SearchHistory> search(BaseSearch<SearchHistory> params) {
        MPJLambdaWrapper<SearchHistory> wrapper = params.wrapper(SearchHistory.class, "t")
                .eq(SearchHistory::getUserId, SecurityUtils.getContextUserId(true))
                .likeRightIfExists(SearchHistory::getContent, params.getKeyword())
                .orderBy(true, params.isAsc(), SearchHistory::getUpdateTime);
        return wrapper.page(params.createPage(), SearchHistory.class);
    }

    @Override
    public boolean createOne(String content) {
        if (StrUtil.isBlank(content)) {
            return false;
        }
        Long contextUserId = SecurityUtils.getContextUserId(true);
        SearchHistory searchHistory = lambdaQuery()
                .eq(SearchHistory::getUserId, contextUserId)
                .eq(SearchHistory::getContent, content)
                .one();
        //每位用户每天第一次搜索才记录热搜次数
        if (searchHistory == null || !DateUtil.isSameDay(searchHistory.getUpdateTime(), new Date())) {
            incrementEs(content);
        }
        //如果用户搜索过，则只增加次数
        if (searchHistory != null) {
            return incrementById(searchHistory.getId(), SearchHistory::getSearchCount, 1);
        } else {
            searchHistory = new SearchHistory();
            searchHistory.setContent(content);
            searchHistory.setSearchCount(1);
            return save(searchHistory);
        }
    }

    /**
     * 记录关键词的搜索次数
     */
    private void incrementEs(String content) {
        String date = DateUtil.formatDate(new Date());
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery(SearchHistoryDoc.Fields.content, content));
        boolQueryBuilder.filter(QueryBuilders.termQuery(SearchHistoryDoc.Fields.date, date));
        queryBuilder.withQuery(boolQueryBuilder);
        try {
            SearchHit<SearchHistoryDoc> searchHit = elasticsearchOperations.searchOne(queryBuilder.build(), SearchHistoryDoc.class);
            SearchHistoryDoc searchHistoryDoc = null;
            if (searchHit != null) {
                searchHistoryDoc = searchHit.getContent();
                searchHistoryDoc.setSearchCount(searchHistoryDoc.getSearchCount() + 1);
            } else {
                searchHistoryDoc = new SearchHistoryDoc();
                searchHistoryDoc.setId(RandomUtil.randomLong());
                searchHistoryDoc.setContent(content);
                searchHistoryDoc.setSearchCount(1L);
                searchHistoryDoc.setDate(date);
            }
            elasticsearchOperations.save(searchHistoryDoc);
        } catch (Exception e) {
            log.error("保存热搜失败", e);
        }
    }

    @Override
    public void removeOne(String content) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        lambdaUpdate().eq(SearchHistory::getUserId, contextUserId)
                .eq(SearchHistory::getContent, content)
                .remove();
    }

    @Override
    public void removeAll() {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        lambdaUpdate().eq(SearchHistory::getUserId, contextUserId).remove();
    }

    @Override
    public List<String> getHotKeys(Date fromDate, Date toDate, int size) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (fromDate != null || toDate != null) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders
                    .rangeQuery(SearchHistoryDoc.Fields.date);
            if (fromDate != null) {
                rangeQueryBuilder.gte(DateUtil.formatDate(fromDate));
            }
            if (toDate != null) {
                rangeQueryBuilder.lte(DateUtil.formatDate(toDate));
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }
        queryBuilder.withQuery(boolQueryBuilder);
//        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders
//                .terms("rank")
//                .field(SearchHistoryDoc.Fields.content)
//                .size(size);
        queryBuilder.withSort(Sort.by(Sort.Direction.DESC, SearchHistoryDoc.Fields.searchCount));
        queryBuilder.withPageable(Pageable.ofSize(size));
        try {
            return elasticsearchOperations.search(queryBuilder.build(), SearchHistoryDoc.class)
                    .get()
                    .map(SearchHit::getContent)
                    .map(SearchHistoryDoc::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询热搜失败", e);
            return Collections.emptyList();
        }
//        try {
//            Aggregations aggregations = (Aggregations) hits.getAggregations().aggregations();
//            Aggregation aggregation = aggregations.asList().get(0);
//            if (aggregation instanceof ParsedStringTerms) {
//                for (Terms.Bucket bucket : ((ParsedStringTerms) aggregation).getBuckets()) {
//                    result.add(bucket.getKeyAsString());
//                }
//            }
//        } catch (Exception ignored) {}
    }

//    @EventListener(SearchEvent.class)
//    public void listen(SearchEvent e) {
//        if (SecurityUtils.getContextUserId(false) != null && StrUtil.isNotBlank(e.getKeyword())) {
//            createOne(e.getKeyword());
//        }
//    }
}