package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.toolkit.JoinWrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.enums.UserFlags;
import pers.tgl.mikufans.domain.user.UserStar;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.domain.video.VideoStar;
import pers.tgl.mikufans.dto.UserStarDto;
import pers.tgl.mikufans.event.EventAction;
import pers.tgl.mikufans.event.VideoStarEvent;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.UserStarMapper;
import pers.tgl.mikufans.model.OrderDto;
import pers.tgl.mikufans.params.UserStarParams;
import pers.tgl.mikufans.service.SysParamService;
import pers.tgl.mikufans.service.UserFlagService;
import pers.tgl.mikufans.service.UserLikeDataService;
import pers.tgl.mikufans.service.UserStarService;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.vo.UserStarVo;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserStarServiceImpl extends BaseServiceImpl<UserStar, UserStarMapper> implements UserStarService {
    /**
     * 用户最大创建收藏夹数量
     */
    private int maxStarCount;

    private final SysParamService sysParamService;
    private final UserFlagService userFlagService;
    private final UserLikeDataService userLikeDataService;

    @PostConstruct
    public void init() {
        maxStarCount = sysParamService.getInt("max_star_count", 10);
    }

    @Override
    public UserStar create(UserStarDto dto) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        UserStar star = BeanUtil.toBean(dto, UserStar.class);
        createDefaultStar(contextUserId);
        long count = countBy(UserStar::getUserId, contextUserId);
        if (count >= maxStarCount) {
            throw new CustomException("您无法创建超过" + maxStarCount + "个收藏夹！");
        }
        star.setSort((int) (count + 1));
        star.setDefFlag(0);
        createUserStar(star);
        UserStarVo userStar = BeanUtil.toBean(star, UserStarVo.class);
        fill(userStar);
        return userStar;
    }

    @Override
    public boolean update(UserStarDto dto) {
        UserStar userStar = getById(dto.getId());
        checkUserPermission(userStar);
        return updateById(BeanUtil.toBean(dto, UserStar.class));
    }

    @Override
    public List<UserStarVo> search(UserStarParams params) {
        Long userId = params.getUserId();
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (userId == null) {
            userId = contextUserId;
        }
        if (userFlagService.isDeny(userId, UserFlags.PUBLIC_STAR)) {
            return Collections.emptyList();
        }
        createDefaultStar(userId);
        List<UserStarVo> list = wrapper()
                .selectAssociation(VideoStar.class, UserStarVo::getVideoStar)
                .leftJoin(VideoStar.class, w ->
                        w.eq(VideoStar::getStarId, UserStar::getId)
                                .eq(VideoStar::getVideoId, params.getVideoId())
                                .eq(VideoStar::getUserId, contextUserId)
                )
                .eq(UserStar::getUserId, userId)
                //如果不是本人只能查看公开的收藏夹
                .eq(!Objects.equals(contextUserId, userId), UserStar::getVisible, 1)
                //优先按指定的排序
                .orderByAsc(UserStar::getSort)
                //最后按创建时间排序
                .orderByDesc(UserStar::getCreateTime)
                .list(UserStarVo.class);
        list.forEach(this::fill);
        return list;
    }

    private void fill(UserStarVo vo) {
        //当前用户是否点赞
        vo.setLikeStatus(userLikeDataService.getStatus(vo.getId()));
        //如果收藏夹没有设置封面,自动设置为收藏的最新的视频的封面
        if (Objects.isNull(vo.getCoverId()) || vo.getCoverId() == 0) {
            Video last = JoinWrappers.lambda(VideoStar.class)
                    .select(Video::getBannerId)
                    .eq(VideoStar::getStarId, vo.getId())
                    .orderByDesc(VideoStar::getCreateTime)
                    .innerJoin(Video.class, Video::getId, VideoStar::getVideoId)
                    .last("limit 1") //必须限制一个
                    .one(Video.class);
            if (last != null) {
                vo.setCoverId(last.getBannerId());
            }
        }
    }

    /**
     * 创建默认收藏夹,排序为第一
     */
    private UserStar createDefaultStar(Long userId) {
        UserStar userStar = lambdaQuery()
                .eq(UserStar::getUserId, userId)
                .eq(UserStar::getDefFlag, 1)
                .one();
        if (userStar == null && userId != null) {
            userStar = new UserStar();
            userStar.setUserId(userId);
            userStar.setSort(0);
            userStar.setDefFlag(1);
            userStar.setStarName("默认收藏夹");
            createUserStar(userStar);
        }
        return userStar;
    }

    private void createUserStar(UserStar userStar) {
        userStar.setVisible(1);
        userStar.setStarCount(0);
        save(userStar);
        userLikeDataService.createLikeData(BusinessType.STAR, userStar.getId());
    }

    @Override
    public boolean removeById(UserStar entity) {
        if (entity != null && entity.getDefFlag() == 1) {
            throw new CustomException("不能删除默认收藏夹");
        }
        return super.removeById(entity);
    }

    @Override
    public void changeOrder(OrderDto dto) {
        if (CollUtil.isEmpty(dto.getIds())) {
            return;
        }
        Long contextUserId = SecurityUtils.getContextUserId(true);
        String ids = StrUtil.join(",", dto.getIds());
        lambdaUpdate().setSql(VideoPart.Fields.sort + " = field(id," + ids + ")")
                .in(UserStar::getId, dto.getIds())
                .eq(UserStar::getUserId, contextUserId)
                .eq(UserStar::getDefFlag, 0)
                .update(new UserStar());
    }

    @Override
    public boolean hasStarVideo(Long videoId) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (contextUserId == null) {
            return false;
        }
        return Db.lambdaQuery(VideoStar.class)
                .eq(VideoStar::getVideoId, videoId)
                .eq(VideoStar::getUserId, contextUserId)
                .exists();
    }

    @Override
    public UserStar getDefaultUserStar() {
        return createDefaultStar(SecurityUtils.getContextUserId(true));
    }

    @EventListener(VideoStarEvent.class)
    public void listen(VideoStarEvent e) {
        Long starId = e.getEntity().getStarId();
        if (e.getAction() == EventAction.INSERT) {
            incrementById(starId, UserStar::getStarCount, 1);
        } else if (e.getAction() == EventAction.DELETE) {
            incrementById(starId, UserStar::getStarCount, -1);
        }
    }
}