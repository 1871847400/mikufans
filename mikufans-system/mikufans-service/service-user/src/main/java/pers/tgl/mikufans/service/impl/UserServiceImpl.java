package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.consts.ExpSource;
import pers.tgl.mikufans.consts.UserSearchSort;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.dto.UserDto;
import pers.tgl.mikufans.es.UserDoc;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.UserMapper;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.params.UserSearchParams;
import pers.tgl.mikufans.service.*;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.vo.UserVo;

import javax.annotation.Resource;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author TGL
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2022-12-31 10:17:31
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<User, UserMapper> implements UserService {
    @Resource
    @Lazy
    private PasswordEncoder passwordEncoder;
    private final UserDynamicService userDynamicService;
    private final AppConfig appConfig;
    private final UserStatsService userStatsService;
    private final UserFollowService userFollowService;
    private final SysParamService sysParamService;
    private final ElasticsearchOperations elasticsearchOperations;
    private final UserFlagService userFlagService;

    @Override
    public UserVo getVoById(Long userId) {
        User user = getById(userId);
        if (user == null) {
            return null;
        }
        UserVo vo = BeanUtil.toBean(user, UserVo.class);
        vo.setFollow(userFollowService.getFollowStatus(userId));
        vo.setNextExp(getLevelMap().getOrDefault(user.getLevel() + 1, 0));
        vo.setFlags(userFlagService.getUserFlags(userId));
        return vo;
    }

    @Override
//    @Cacheable(value = "userIdCache", key = "#username", unless = "#result == null")
    public Long getUserIdByName(String username) {
        User user = lambdaQuery()
                .select(User::getId)
                .eq(User::getUsername, username)
                .one();
        return user != null ? user.getId() : null;
    }

    @Override
    public String getUsernameByNick(String nickname) {
        User user = lambdaQuery().eq(User::getNickname, nickname)
                .select(User::getUsername).one();
        return user != null ? user.getUsername() : null;
    }

    @Override
    public int getUserLevel(Long userId) {
        if (userId == null) {
            userId = SecurityUtils.getContextUserId(false);
            if (userId == null) {
                return 0;
            }
        }
        return Optional.ofNullable(getColumnValue(userId, User::getLevel))
                .orElse(0);
    }

    @Override
    public void giveExp(Long userId, int delta, ExpSource expSource) {
        if (delta == 0 || userId == null) {
            return;
        }
        incrementById(userId, User::getExp, delta);
        Integer exp = getColumnValue(userId, User::getExp);
        if (exp == null) {
            return;
        }
        Map<Integer, Integer> levelMap = getLevelMap();
        for (Map.Entry<Integer, Integer> e : levelMap.entrySet()) {
            if (exp >= e.getValue()) {
                lambdaUpdate().set(User::getLevel, e.getKey())
                        .eq(User::getId, userId)
                        //等级只会升不会降
                        .lt(User::getLevel, e.getKey())
                        .update();
                break;
            }
        }
    }

    private Map<Integer, Integer> getLevelMap() {
        Map<Integer, Integer> result = new LinkedHashMap<>();
        //降序查找等级参数
        Map<String, String> map = sysParamService.getMap("user_level_%",  false);
        for (Map.Entry<String, String> e : map.entrySet()) {
            result.put(Integer.parseInt(e.getKey().replace("user_level_", "")),
                    Integer.parseInt(e.getValue()));
        }
        return result;
    }

    @Override
    public PageImpl<UserVo> search(UserSearchParams params) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        List<HighlightBuilder.Field> highlightFields = new ArrayList<>(1);
        if (StrUtil.isNotBlank(params.getNickname())) {
            boolQuery.filter(QueryBuilders.matchQuery(UserDoc.Fields.nickname, params.getNickname()));
            highlightFields.add(new HighlightBuilder.Field(UserDoc.Fields.nickname)
                    .preTags("<em>")
                    .postTags("</em>"));
        }
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withHighlightFields(highlightFields);
        if (params.getPageSize() == 0) {
            return params.page(elasticsearchOperations.count(queryBuilder.build(), UserDoc.class));
        } else {
            queryBuilder.withPageable(params.pageRequest());
        }
        if (params.getSort() != UserSearchSort.DEFAULT) {
            queryBuilder.withSort(Sort.by(params.getSort().getDirection(), params.getSort().getProperty()));
        }
        SearchHits<UserDoc> searchHits = elasticsearchOperations.search(queryBuilder.build(), UserDoc.class);
        List<UserVo> records = searchHits
                .get()
                .map(hit -> {
                    UserVo vo = BeanUtil.copyProperties(hit.getContent(), UserVo.class);
                    if (params.isLastDynamic()) {
                        vo.setLastDynamic(userDynamicService.getLastDynamic(vo.getId()));
                    }
                    vo.setFollow(userFollowService.getFollowStatus(vo.getId()));
                    List<String> list = hit.getHighlightField(UserDoc.Fields.nickname);
                    if (CollUtil.isNotEmpty(list)) {
                        vo.setHighlightNickname(list.get(0));
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        return params.page(records, searchHits.getTotalHits());
    }

    @Override
    public List<UserDoc> getAutoComplete(String keyword) {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(UserDoc.Fields.nickname, keyword))
                .withPageable(PageRequest.ofSize(10))
                .build();
        return elasticsearchOperations.search(query, UserDoc.class)
                .get()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public User createUser(String username, String nickname, String password) {
        Long createBy = SecurityUtils.getContextUserId(false);
        if (createBy == null) {
            createBy = 0L;
        }
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword(password);
        user.setCoin(appConfig.getVideo().getCoin().getInitialCount());
        user.setBackground("默认");
        user.setPerms(Collections.emptyList());
        user.setCreateBy(createBy);
        save(user);
        return getById(user.getId());
    }

    @Override
    public boolean updateUser(UserDto dto) {
        User user = getById(dto.getId());
        UserToken userToken = SecurityUtils.getContextUserToken();
        if (user == null || userToken == null) {
            throw new CustomException("用户不存在");
        }
        if (!Objects.equals(userToken.getId(), user.getId()) && !userToken.isAdmin()) {
            throw new CustomException(Code.FORBIDDEN);
        }
        if (StrUtil.isNotBlank(dto.getNickname())) {
            boolean exists = lambdaQuery()
                    .eq(User::getNickname, dto.getNickname())
                    .ne(User::getId, user.getId())
                    .exists();
            if (exists) {
                throw new CustomException("该昵称已经存在了");
            }
        }
        return updateById(BeanUtil.toBean(dto, User.class));
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        try {
            Image image = ImgUtil.getImage(new URL(avatarUrl));
        } catch (MalformedURLException ignored) {}
    }
}