package pers.tgl.mikufans.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.domain.enums.UserFlags;
import pers.tgl.mikufans.domain.user.UserStar;
import pers.tgl.mikufans.domain.video.VideoStar;
import pers.tgl.mikufans.dto.VideoStarDto;
import pers.tgl.mikufans.es.VideoStarDoc;
import pers.tgl.mikufans.event.EventAction;
import pers.tgl.mikufans.event.VideoStarEvent;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.VideoStarMapper;
import pers.tgl.mikufans.model.BaseDoc;
import pers.tgl.mikufans.params.VideoStarParams;
import pers.tgl.mikufans.service.*;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.vo.VideoStarVo;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoStarServiceImpl extends BaseServiceImpl<VideoStar, VideoStarMapper> implements VideoStarService {
    private final VideoService videoService;
    private final UserStarService userStarService;
    private final SysParamService sysParamService;
    private final UserFlagService userFlagService;
    private final ElasticsearchOperations elasticsearchOperations;

    private int STAR_MAX_VIDEO;

    @PostConstruct
    public void init() {
        STAR_MAX_VIDEO = sysParamService.getInt("star_max_video", 200);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDto(VideoStarDto dto) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        if (CollUtil.isNotEmpty(dto.getDelList())) {
            for (Long starId : dto.getDelList()) {
                VideoStar videoStar = lambdaQuery().eq(VideoStar::getStarId, starId)
                        .eq(VideoStar::getVideoId, dto.getVideoId())
                        .one();
                removeById(videoStar);
            }
        }
        if (CollUtil.isNotEmpty(dto.getAddList())) {
            for (Long starId : dto.getAddList()) {
                createVideoStar(dto.getVideoId(), starId);
            }
        }
        return lambdaQuery().eq(VideoStar::getUserId, contextUserId)
                .eq(VideoStar::getVideoId, dto.getVideoId())
                .exists();
    }

    @Override
    public PageImpl<VideoStarVo> search(VideoStarParams params) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        UserStar userStar = userStarService.getById(params.getStarId());
        /**
         * 1.收藏夹不存在
         * 2.未公开所有收藏夹
         * 3.未公开当前收藏夹
         */
        if (userStar == null || userFlagService.isDeny(userStar.getUserId(), UserFlags.PUBLIC_STAR)
                || userStar.getVisible()==0 && !Objects.equals(userStar.getUserId(), contextUserId)) {
            return PageImpl.empty();
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(VideoStarDoc.Fields.starId, userStar.getId()));
        if (StrUtil.isNotBlank(params.getTitle())) {
            boolQuery.filter(QueryBuilders.matchQuery(VideoStarDoc.Fields.title, params.getTitle()));
        }
        List<HighlightBuilder.Field> highlightFields = new ArrayList<>(1);
        highlightFields.add(new HighlightBuilder.Field(VideoStarDoc.Fields.title)
                .requireFieldMatch(false)
                .preTags("<em>")
                .postTags("</em>"));
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withHighlightFields(highlightFields)
                .withSort(Sort.by(params.getDir(), BaseDoc.Fields.createTime))
                .withPageable(params.pageRequest())
                .build();
        SearchHits<VideoStarDoc> hits = elasticsearchOperations.search(query, VideoStarDoc.class);
        List<VideoStarVo> records = hits.stream().map(hit -> {
            VideoStarVo videoStar = getById(hit.getId(), VideoStarVo.class);
            if (videoStar == null) {
                return null;
            }
            List<String> highlightField = hit.getHighlightField(VideoStarDoc.Fields.title);
            if (CollUtil.isNotEmpty(highlightField)) {
                videoStar.setHighlighted(highlightField.get(0));
            }
            videoStar.setVideo(videoService.getVoById(videoStar.getVideoId()));
            return videoStar;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return params.page(records, hits.getTotalHits());
    }

    private void createVideoStar(Long videoId, Long starId) {
        UserStar userStar;
        if (starId == 0) {
            userStar = userStarService.getDefaultUserStar();
        } else {
            userStar = Db.getById(starId, UserStar.class);
        }
        checkUserPermission(userStar);
        if (userStar.getStarCount() >= STAR_MAX_VIDEO) {
            throw new CustomException("单个收藏夹最多收藏" + STAR_MAX_VIDEO + "个视频");
        }
        VideoStar videoStar = new VideoStar();
        videoStar.setVideoId(videoId);
        videoStar.setStarId(userStar.getId());
        try {
            save(videoStar);
        } catch (DuplicateKeyException e) {
            return; //无需处理
        }
        int starCount = getStarCount(videoId);
        publishEvent(new VideoStarEvent(this, videoStar, EventAction.INSERT, starCount));
    }

    @Override
    public boolean removeById(VideoStar entity) {
        if (super.removeById(entity)) {
            int starCount = getStarCount(entity.getVideoId());
            publishEvent(new VideoStarEvent(this, entity, EventAction.DELETE, starCount));
            return true;
        }
        return false;
    }

    /**
     * 获取当前用户对指定视频的收藏次数
     */
    public int getStarCount(Long videoId) {
        return Db.lambdaQuery(VideoStar.class)
                .eq(VideoStar::getVideoId, videoId)
                .eq(VideoStar::getUserId, SecurityUtils.getContextUserId(true))
                .count().intValue();
    }
}