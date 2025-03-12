package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.domain.user.UserFollow;
import pers.tgl.mikufans.dto.UserDynamicDto;
import pers.tgl.mikufans.dto.UserDynamicShareDto;
import pers.tgl.mikufans.es.UserDynamicDoc;
import pers.tgl.mikufans.event.EventAction;
import pers.tgl.mikufans.event.UserDynamicEvent;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.UserDynamicMapper;
import pers.tgl.mikufans.model.BaseDoc;
import pers.tgl.mikufans.params.UserDynamicParams;
import pers.tgl.mikufans.service.UserCommentAreaService;
import pers.tgl.mikufans.service.UserDynamicService;
import pers.tgl.mikufans.service.UserFollowService;
import pers.tgl.mikufans.service.UserLikeDataService;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.RedisUtils;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.vo.UserDynamicVo;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDynamicServiceImpl extends BaseServiceImpl<UserDynamic, UserDynamicMapper> implements UserDynamicService {
    private final UserFollowService userFollowService;
    private final UserLikeDataService userLikeDataService;
    private final UserCommentAreaService userCommentAreaService;
    private final ElasticsearchOperations elasticsearchOperations;
    private final RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDynamic createDynamic(BusinessType businessType, Long businessId, @Nullable UserDynamicDto dto) {
        if (dto == null) {
            dto = new UserDynamicDto();
            dto.setPublishFlag(0);
            dto.setPublishTime(new Date());
            dto.setVisible(1);
        } else {
            //创建动态时 必须大于当前时间 2小时 <15天
            if (dto.getPublishTime() != null) {
                long between = DateUtil.between(new Date(), dto.getPublishTime(), DateUnit.HOUR);
                if (between < 2 || between > 24 * 15) {
                    throw new CustomException("定时发布时间只能在未来2小时-15天");
                }
            }
        }
        UserDynamic userDynamic = BeanUtil.toBean(dto, UserDynamic.class);
        userDynamic.setTop(0);
        userDynamic.setShareId(0L);
        userDynamic.setShareReason("");
        userDynamic.setShares(0);
        userDynamic.setDynamicType(businessType);
        userDynamic.setTargetId(businessId);
        userDynamic.setDisabled(0);
        save(userDynamic);
        //需要专门为动态创建评论和点赞
        userCommentAreaService.createDto(BusinessType.DYNAMIC, userDynamic.getId(), dto.getCommentArea());
        userLikeDataService.createLikeData(BusinessType.DYNAMIC, userDynamic.getId());
        //立即加入es,防止刷新动态看不到
        elasticsearchOperations.save(new UserDynamicDoc(userDynamic));
        return userDynamic;
    }

    @Override
    public boolean updateDynamic(Long targetId, SFunction<UserDynamic, ?> col, Object val) {
        return lambdaUpdate().set(col, val)
                .eq(UserDynamic::getTargetId, targetId)
                .update(new UserDynamic());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDto(UserDynamicDto dto) {
        UserDynamic userDynamic = getById(dto.getId());
        checkUserPermission(userDynamic);

        //如果动态已经成功发布,则无法再修改定时发布时间
        if (userDynamic.getPublishFlag() == 0 && userDynamic.getDisabled() == 0
                && Objects.equals(dto.getPublishFlag(), 1)) {
            throw new CustomException("已无法修改发布时间");
        }

        //如果修改定时发布时间，必须>创建时间2小时和当前时间 <15天
        if (dto.getPublishTime() != null && !userDynamic.getPublishTime().equals(dto.getPublishTime())) {
            if (dto.getPublishTime().before(new Date())) {
                throw new CustomException("发布时间不能在过去");
            }
            long between = DateUtil.between(userDynamic.getCreateTime(), dto.getPublishTime(), DateUnit.HOUR);
            if (between < 2 || between > 24 * 15) {
                throw new CustomException("定时发布时间只能在未来2小时-15天");
            }
        }

        //如果从定时发布改为非定时发布
        if (userDynamic.getPublishFlag() == 1 && Objects.equals(dto.getPublishFlag(), 0)) {
            dto.setPublishTime(new Date());
        }

        //修改评论区设置
        if (dto.getCommentArea() != null) {
            userCommentAreaService.updateDto(dto.getCommentArea());
        }
        updateById(BeanUtil.toBean(dto, UserDynamic.class));
    }

    private void fillDefaultProperties(UserDynamicVo vo) {
        vo.setCommentArea(userCommentAreaService.getCommentArea(vo.getId()));
        vo.setLikeStatus(userLikeDataService.getStatus(vo.getId()));
    }

    @Override
    public PageImpl<UserDynamicVo> search(UserDynamicParams params) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(UserDynamicDoc.Fields.publishFlag, 0))
                .filter(QueryBuilders.termQuery(UserDynamicDoc.Fields.disabled, 0));
        //如果当前已登录,则要求动态 要么公开 要么是自己的
        if (contextUserId != null) {
            BoolQueryBuilder should = QueryBuilders.boolQuery()
                    .should(QueryBuilders.termQuery(UserDynamicDoc.Fields.visible, 1))
                    .should(QueryBuilders.termQuery(BaseDoc.Fields.userId, contextUserId));
            boolQuery.should(should);
        } else {
            //如果没有登录,则要求公开
            boolQuery.filter(QueryBuilders.termQuery(UserDynamicDoc.Fields.visible, 1));
        }
        if (params.getUserId() != null) {
            boolQuery.filter(QueryBuilders.termQuery(BaseDoc.Fields.userId, params.getUserId()));
        } else if (contextUserId != null) {
            List<Long> followIds = userFollowService.getFollowIds(contextUserId);
            if (!params.isExcludeSelf()) {
                followIds.add(contextUserId);
            }
            if (followIds.isEmpty()) {
                return PageImpl.empty();
            }
            boolQuery.filter(QueryBuilders.termsQuery(BaseDoc.Fields.userId, followIds));
        } else {
            return PageImpl.empty();
        }
        if (params.getDynamicType() != null) {
            boolQuery.filter(QueryBuilders.termQuery(UserDynamicDoc.Fields.dynamicType, params.getDynamicType()));
        }
        if (isValidId(params.getShareId())) {
            boolQuery.filter(QueryBuilders.termQuery(UserDynamicDoc.Fields.shareId, params.getShareId()));
        }
        if (StrUtil.isNotBlank(params.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery(UserDynamicDoc.Fields.content, params.getKeyword()));
        }

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSort(Sort.by(Sort.Direction.DESC, UserDynamicDoc.Fields.publishTime))
                .withPageable(params.pageRequest())
                .build();
        SearchHits<UserDynamicDoc> hits = elasticsearchOperations.search(query, UserDynamicDoc.class);
        List<UserDynamicVo> records = hits.stream()
                .map(SearchHit::getId)
                .map(this::getVoById)
                .collect(Collectors.toList());
        //如果搜索指定用户的动态并且是关注用户,则更新访问时间
        if (params.getUserId() != null && contextUserId != null) {
            userFollowService.createAccess(params.getUserId());
        }
        //记录搜索动态的时间
        if (contextUserId != null) {
            redisUtils.set("search-dynamic:" + contextUserId, System.currentTimeMillis(), Duration.ofDays(1));
        }
        return params.page(records, hits.getTotalHits());
    }

    @Override
    public UserDynamicVo getVoById(Object id) {
        UserDynamicVo vo = getById(id, UserDynamicVo.class);
        if (vo == null) {
            vo = wrapper().eq(UserDynamic::getTargetId, id)
                    .eq(UserDynamic::getShareId, 0)
                    .one(UserDynamicVo.class);
        }
        if (vo != null) {
            fillDefaultProperties(vo);
        }
        return vo;
    }

    @Override
    public UserDynamicVo createShare(UserDynamicShareDto dto) {
        UserDynamic targetDynamic = getById(dto.getShareId());
        if (targetDynamic.getShareId() != 0) {
            targetDynamic = getById(targetDynamic.getShareId());
            if (targetDynamic == null) {
                throw new CustomException(Code.RESOURCE_NOT_FOUND);
            }
        }
        UserDynamic userDynamic = BeanUtil.toBean(dto, UserDynamic.class);
        userDynamic.setTargetId(targetDynamic.getTargetId());
        userDynamic.setDynamicType(targetDynamic.getDynamicType());
        userDynamic.setDisabled(0);
        userDynamic.setTop(0);
        userDynamic.setPublishFlag(0);
        userDynamic.setPublishTime(new Date());
        save(userDynamic);
        userCommentAreaService.createDto(BusinessType.DYNAMIC, userDynamic.getId(), null);
        userLikeDataService.createLikeData(BusinessType.DYNAMIC, userDynamic.getId());
        elasticsearchOperations.save(new UserDynamicDoc(userDynamic, ""));
        incrementById(targetDynamic.getId(), UserDynamic::getShares, 1);
        return getVoById(userDynamic.getId());
    }

    @Override
    public UserDynamic getLastDynamic(Long userId) {
        return wrapper().eq(UserDynamic::getUserId, userId)
                .eq(UserDynamic::getDisabled, 0)
                .eq(UserDynamic::getPublishFlag, 0)
                .eq(UserDynamic::getVisible, 1)
                .orderByDesc(UserDynamic::getPublishTime)
                .first();
    }

    @Override
    public int getUnread(@Nullable BusinessType dynamicType) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (contextUserId == null) {
            return 0;
        }
        String searchTime = redisUtils.getString("search-dynamic:" + contextUserId, "0");
        return wrapper()
                .distinct()
                .select(UserDynamic::getId)
                .innerJoin(UserFollow.class, builder ->
                        builder.eq(UserFollow::getUserId, contextUserId)
                                .eq(UserFollow::getTargetId, UserDynamic::getUserId))
                .eqIfExists(UserDynamic::getDynamicType, dynamicType)
                .eq(UserDynamic::getDisabled, 0)
                .eq(UserDynamic::getPublishFlag, 0)
                .eq(UserDynamic::getVisible, 1)
                .gt(UserDynamic::getPublishTime, UserFollow::getAccessTime)
                .gt(UserDynamic::getPublishTime, NumberUtil.parseLong(searchTime))
                .count()
                .intValue();
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id) || super.removeById(getOneBy(UserDynamic::getTargetId, id));
    }

    @Override
    public boolean removeById(UserDynamic entity) {
        if (super.removeById(entity)) {
            elasticsearchOperations.delete(entity.getId()+"", UserDynamicDoc.class);
            publishEvent(new UserDynamicEvent(this, entity, EventAction.DELETE));
            return true;
        }
        return false;
    }

    @Override
    public boolean save(UserDynamic entity) {
        if (super.save(entity)) {
            publishEvent(new UserDynamicEvent(this, entity, EventAction.INSERT));
            return true;
        }
        return false;
    }

    /**
     * 自动修改定时发布标记
     */
    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.SECONDS)
    public void check() {
        lambdaUpdate().set(UserDynamic::getPublishFlag, 0)
                .eq(UserDynamic::getPublishFlag, 1)
                .lt(UserDynamic::getPublishTime, new Date())
                .update(new UserDynamic());
    }
}