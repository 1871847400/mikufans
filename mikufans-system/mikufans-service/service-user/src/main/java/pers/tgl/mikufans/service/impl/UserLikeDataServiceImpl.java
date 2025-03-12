package pers.tgl.mikufans.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.message.MsgUnread;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.domain.user.UserLike;
import pers.tgl.mikufans.domain.user.UserLikeData;
import pers.tgl.mikufans.event.UserLikeEvent;
import pers.tgl.mikufans.mapper.UserLikeDataMapper;
import pers.tgl.mikufans.model.SearchParams;
import pers.tgl.mikufans.service.MessageService;
import pers.tgl.mikufans.service.MsgUnreadService;
import pers.tgl.mikufans.service.UserLikeDataService;
import pers.tgl.mikufans.service.UserLikeService;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.vo.LikeStatus;
import pers.tgl.mikufans.vo.UserLikeDataVo;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLikeDataServiceImpl extends BaseServiceImpl<UserLikeData, UserLikeDataMapper> implements UserLikeDataService {
    private final UserLikeService userLikeService;
    private final MessageService messageService;
    private final MsgUnreadService msgUnreadService;

    @Override
    public Long createLikeData(BusinessType likeType, Long businessId) {
        UserLikeData data = new UserLikeData();
        data.setLikeType(likeType);
        data.setBusiId(businessId);
        data.setLikes(0);
        data.setDislikes(0);
        data.setLikeUserIds(Collections.emptyList());
        data.setHidden(0);
        data.setReadFlag(1);
        save(data);
        return data.getId();
    }

    @Override
    public UserLikeDataVo getLikeData(Long id) {
        UserLikeDataVo likeData = wrapper()
                .disableLogicDel()
                .eq(UserLikeData::getId, id)
                .or()
                .eq(UserLikeData::getBusiId, id)
                .one(UserLikeDataVo.class);
        if (likeData != null) {
            fill(likeData);
        }
        return likeData;
    }

    @Override
    public LikeStatus getStatus(Long id) {
        LikeStatus likeStatus = wrapper()
                .select(UserLikeData::getId, UserLikeData::getLikes, UserLikeData::getDislikes)
                .eq(UserLikeData::getId, id)
                .or()
                .eq(UserLikeData::getBusiId, id)
                .one(LikeStatus.class);
        if (likeStatus == null) {
            likeStatus = new LikeStatus();
            likeStatus.setLikes(0);
            likeStatus.setDislikes(0);
            likeStatus.setLikeVal(0);
        } else {
            likeStatus.setLikeVal(userLikeService.getLikeVal(likeStatus.getId()));
        }
        return likeStatus;
    }

    @Override
    public PageImpl<UserLikeDataVo> search(SearchParams params) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        PageImpl<UserLikeDataVo> page = wrapper()
                .eq(UserLikeData::getUserId, contextUserId)
                .gt(UserLikeData::getLikes, 0)
                .orderByAsc(UserLikeData::getReadFlag, UserLikeData::getHidden)
                .orderByDesc(UserLikeData::getUpdateTime)
                .page(params.page(), UserLikeDataVo.class);
        page.fill(this::fill);
        List<Long> readIds = page.getRecords()
                .stream()
                .filter(a -> a.getReadFlag() == 0)
                .map(UserLikeData::getId)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(readIds)) {
            lambdaUpdate().set(UserLikeData::getReadFlag, 1)
                    .in(UserLikeData::getId, readIds)
                    .update(new UserLikeData());
            msgUnreadService.setUnreadCount(MsgUnread::getLikes, getUnreadLike(contextUserId), contextUserId);
        }
        return page;
    }

    private void fill(UserLikeDataVo likeData) {
        likeData.setLikeVal(userLikeService.getLikeVal(likeData.getId()));
        likeData.setSource(messageService.getModel(likeData.getLikeType(), likeData.getBusiId()));
        if (likeData.getLikeType() == BusinessType.DYNAMIC) {
            UserDynamic dynamic = Db.getById(likeData.getBusiId(), UserDynamic.class);
            if (dynamic != null) {
                likeData.setLikeLabel(dynamic.getDynamicType().getLabel());
            }
        } else {
            likeData.setLikeLabel(likeData.getLikeType().getLabel());
        }
    }

    @EventListener(UserLikeEvent.class)
    public void listen(UserLikeEvent e) {
        Long likeDataId = e.getNewLike().getLikeDataId();
        Long userId = e.getNewLike().getUserId();
        UserLikeData likeData = getById(likeDataId);
        if (likeData == null) {
            return;
        }
        if (e.getOldLike() != null) {
            Integer likeVal = e.getOldLike().getLikeVal();
            if (Objects.equals(likeVal, UserLike.LIKE_VAL)) {
                if (likeData.getLikeUserIds().remove(userId)) {
                    updateById(likeDataId, UserLikeData::getLikeUserIds, likeData.getLikeUserIds());
                }
                incrementById(likeDataId, UserLikeData::getLikes, -1);
            } else if (Objects.equals(likeVal, UserLike.DISLIKE_VAL)) {
                incrementById(likeDataId, UserLikeData::getDislikes, -1);
            }
        }
        Integer likeVal = e.getNewLike().getLikeVal();
        if (Objects.equals(likeVal, UserLike.LIKE_VAL)) {
            List<Long> likeUserIds = likeData.getLikeUserIds();
            likeUserIds.add(0, userId);
            likeUserIds = likeUserIds.stream()
                    .distinct()
                    .limit(2)
                    .collect(Collectors.toList());
            UserLikeData newLikeData = new UserLikeData();
            newLikeData.setId(likeDataId);
            newLikeData.setLikeUserIds(likeUserIds);
            newLikeData.setLikes(likeData.getLikes() + 1);
            newLikeData.setLikeTime(new Date());
            newLikeData.setReadFlag(0);
            updateById(newLikeData);
            msgUnreadService.setUnreadCount(MsgUnread::getLikes, getUnreadLike(likeData.getUserId()), likeData.getUserId());
        } else if (Objects.equals(likeVal, UserLike.DISLIKE_VAL)) {
            incrementById(likeDataId, UserLikeData::getDislikes, 1);
        }
    }

    public int getUnreadLike(Long userId) {
        return lambdaQuery().eq(UserLikeData::getReadFlag, 0)
                .eq(UserLikeData::getUserId, userId)
                .gt(UserLikeData::getLikes, 0)
                .count().intValue();
    }
}