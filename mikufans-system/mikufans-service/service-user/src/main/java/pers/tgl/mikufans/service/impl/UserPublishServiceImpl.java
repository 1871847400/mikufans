package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.user.UserPublish;
import pers.tgl.mikufans.dto.UserPublishDto;
import pers.tgl.mikufans.event.EventAction;
import pers.tgl.mikufans.event.UserDynamicEvent;
import pers.tgl.mikufans.event.UserPublishEvent;
import pers.tgl.mikufans.mapper.UserPublishMapper;
import pers.tgl.mikufans.service.UserDynamicService;
import pers.tgl.mikufans.service.UserPublishService;

@Service
@RequiredArgsConstructor
public class UserPublishServiceImpl extends BaseServiceImpl<UserPublish, UserPublishMapper> implements UserPublishService {
    private final UserDynamicService userDynamicService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserPublish createDto(UserPublishDto dto) {
        UserPublish userPublish = BeanUtil.toBean(dto, UserPublish.class);
        save(userPublish);
        userDynamicService.createDynamic(BusinessType.PUBLISH, userPublish.getId(), dto.getDynamic());
        publishEvent(new UserPublishEvent(this, userPublish, EventAction.INSERT));
        return userPublish;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDto(UserPublishDto dto) {
        UserPublish userPublish = getById(dto.getId());
        checkUserPermission(userPublish);
        if (dto.getDynamic() != null) {
            userDynamicService.updateDto(dto.getDynamic());
        }
        updateById(BeanUtil.toBean(dto, UserPublish.class));
    }

    @Override
    public boolean removeById(UserPublish entity) {
        if (super.removeById(entity)) {
            userDynamicService.removeById(entity.getId());
            publishEvent(new UserPublishEvent(this, entity, EventAction.DELETE));
            return true;
        }
        return false;
    }

    @EventListener(UserDynamicEvent.class)
    public void listen(UserDynamicEvent e) {
        if (e.isDelete() && e.getEntity().getDynamicType() == BusinessType.PUBLISH) {
            removeById(e.getEntity().getTargetId());
        }
    }
}