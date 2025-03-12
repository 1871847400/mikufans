package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.consts.FollowUserSort;
import pers.tgl.mikufans.consts.UserFollowStatus;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.enums.UserFlags;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.domain.user.UserFollow;
import pers.tgl.mikufans.es.UserDoc;
import pers.tgl.mikufans.event.FollowEvent;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.UserFollowMapper;
import pers.tgl.mikufans.model.BaseDoc;
import pers.tgl.mikufans.params.UserFollowParams;
import pers.tgl.mikufans.service.UserDynamicService;
import pers.tgl.mikufans.service.UserFlagService;
import pers.tgl.mikufans.service.UserFollowService;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.util.ServletUtils;
import pers.tgl.mikufans.vo.UserFollowVo;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFollowServiceImpl extends BaseServiceImpl<UserFollow, UserFollowMapper> implements UserFollowService {
    private final ElasticsearchOperations elasticsearchOperations;
    private final UserFlagService userFlagService;
    @Resource
    @Lazy
    private UserDynamicService userDynamicService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserFollowStatus createFollow(long targetId) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        if (Objects.equals(contextUserId, targetId)) {
            throw new CustomException("不能关注自己");
        }
        try {
            UserFollow userFollow = new UserFollow();
            userFollow.setUserId(contextUserId);
            userFollow.setTargetId(targetId);
            userFollow.setAccessCount(0);
            userFollow.setAccessTime(new Date());
            save(userFollow);
            publishEvent(new FollowEvent(this, userFollow, false));
            return getFollowStatus(targetId);
        } catch (DuplicateKeyException e) {
            return getFollowStatus(targetId);
        }
    }

    @Override
    public boolean cancelFollow(long targetId) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        UserFollow one = lambdaQuery().select(UserFollow::getId)
                .eq(UserFollow::getUserId, contextUserId)
                .eq(UserFollow::getTargetId, targetId)
                .one();
        return one != null && getService(UserFollowService.class).removeById(one.getId());
    }

    @Override
    public boolean removeById(UserFollow entity) {
        if (super.removeById(entity)) {
            publishEvent(new FollowEvent(this, entity, true));
            return true;
        }
        return false;
    }

    @Override
    public boolean isFollow(long targetId) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        return lambdaQuery().eq(UserFollow::getUserId, contextUserId)
                .eq(UserFollow::getTargetId, targetId)
                .exists();
    }

    @Override
    public UserFollowStatus getFollowStatus(Long targetId) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (contextUserId == null || targetId == null) {
            return UserFollowStatus.UNFOLLOWED;
        }
        boolean first = lambdaQuery()
                .eq(UserFollow::getUserId, contextUserId)
                .eq(UserFollow::getTargetId, targetId)
                .exists();
        if (!first) {
            return UserFollowStatus.UNFOLLOWED;
        }
        boolean second = lambdaQuery()
                .eq(UserFollow::getTargetId, contextUserId)
                .eq(UserFollow::getUserId, targetId)
                .exists();
        return second ? UserFollowStatus.EACH_FOLLOWED : UserFollowStatus.FOLLOWED;
    }

    @Override
    public UserFollow getFollow(long targetId) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (contextUserId == null || Objects.equals(contextUserId, targetId)) {
            return null;
        }
        return lambdaQuery().eq(UserFollow::getUserId, contextUserId)
                .eq(UserFollow::getTargetId, targetId)
                .one();
    }

    @Override
    public List<Long> getFollowIds(@Nullable Long userId, FollowUserSort sort, boolean asc) {
        if (userId == null) {
            userId = SecurityUtils.getContextUserId(true);
        }
        //这里使用的别名需要和sort枚举里的一致
        return new MPJLambdaWrapper<>(UserFollow.class, "t")
                .distinct()
                .selectAll(UserFollow.class)
                .select(UserDynamic::getCreateTime)
                .eq(UserFollow::getUserId, userId)
                //可能对方没有发过动态
                .leftJoin(UserDynamic.class, "t1", UserDynamic::getUserId, UserFollow::getTargetId)
                .orderBy(true, asc, sort.getAlias(), sort.getColumn())
                .list()
                .stream()
                .map(UserFollow::getTargetId)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserFollowVo> getFollows(UserFollowParams params) {
        Long userId = params.getUserId();
        if (userId == null) {
            userId = SecurityUtils.getContextUserId(true);
        }
        if (userFlagService.isDeny(userId, UserFlags.PUBLIC_FOLLOW)) {
            return PageImpl.empty();
        }
        PageImpl<UserFollowVo> page = params.page();
        List<Long> followIds = getFollowIds(userId, params.getSort(), BooleanUtil.isTrue(params.getAsc()));
        if (CollUtil.isEmpty(followIds)) {
            return page;
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        List<HighlightBuilder.Field> highlightFields = new ArrayList<>(1);
        if (StrUtil.isNotBlank(params.getKeyword())) {
            boolQuery.filter(QueryBuilders.matchPhraseQuery(UserDoc.Fields.nickname, params.getKeyword()));
            //高亮名称中匹配的字段
            highlightFields.add(new HighlightBuilder.Field(UserDoc.Fields.nickname)
                    .preTags("<em>")
                    .postTags("</em>"));
        }
        //要求id在关注列表中
        boolQuery.filter(QueryBuilders.termsQuery(BaseDoc.Fields.id, followIds));
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withHighlightFields(highlightFields)
                .withPageable(PageRequest.of((int) (page.getCurrent() - 1), (int) page.getSize()))
                .build();
        SearchHits<UserDoc> search = elasticsearchOperations.search(query, UserDoc.class);
        List<UserFollowVo> records = search.get()
                .sorted(Comparator.comparingInt(a -> followIds.indexOf(a.getContent().getId())))
                .map(hit -> {
                    UserFollowVo vo = BeanUtil.toBean(hit.getContent(), UserFollowVo.class);
                    List<String> list = hit.getHighlightField(UserDoc.Fields.nickname);
                    if (CollUtil.isNotEmpty(list)) {
                        vo.setHighlighted(list.get(0));
                    }
                    vo.setFollow(getFollowStatus(vo.getId()));
                    vo.setNews(hasNews(vo.getId()));
                    return vo;
                })
                .collect(Collectors.toList());
        page.setRecords(records);
        page.setTotal(search.getTotalHits());
        return page;
    }

    /**
     * 关注用户是否有最新动态，自己还没查看
     */
    private boolean hasNews(Long targetUserId) {
        UserDynamic lastDynamic = userDynamicService.getLastDynamic(targetUserId);
        UserFollow follow = getFollow(targetUserId);
        return follow != null && lastDynamic != null && lastDynamic.getCreateTime().after(follow.getAccessTime());
    }

    @Override
    public IPage<UserFollowVo> getFans(Long userId) {
        if (userId == null) {
            userId = SecurityUtils.getContextUserId(true);
        }
        if (userFlagService.isDeny(userId, UserFlags.PUBLIC_FANS)) {
            return PageImpl.empty();
        }
        return new MPJLambdaWrapper<>(UserFollow.class)
                .selectAll(User.class)
                .eq(UserFollow::getTargetId, userId)
                .innerJoin(User.class, User::getId, UserFollow::getUserId)
                .orderBy(true, ServletUtils.getBoolParameter("asc", false), UserFollow::getCreateTime)
                .page(BaseController.createPage(), UserFollowVo.class)
                .convert(user->{
                    user.setFollow(getFollowStatus(user.getId()));
                    return user;
                });
    }

    @Override
    public void createAccess(Long targetUserId) {
        lambdaUpdate()
                .eq(UserFollow::getUserId, SecurityUtils.getContextUserId(true))
                .eq(UserFollow::getTargetId, targetUserId)
                .setIncrBy(UserFollow::getAccessCount, 1)
                .set(UserFollow::getAccessTime, new Date())
                .update(new UserFollow());
    }
}