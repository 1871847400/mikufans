package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.enums.CommentFlag;
import pers.tgl.mikufans.domain.comment.UserCommentArea;
import pers.tgl.mikufans.dto.CommentAreaDto;
import pers.tgl.mikufans.event.EventAction;
import pers.tgl.mikufans.event.UserCommentEvent;
import pers.tgl.mikufans.event.UserCommentTopEvent;
import pers.tgl.mikufans.mapper.UserCommentAreaMapper;
import pers.tgl.mikufans.service.UserCommentAreaService;

import static pers.tgl.mikufans.consts.Consts.USER_MIN_LEVEL;

@Service
public class UserCommentAreaServiceImpl extends BaseServiceImpl<UserCommentArea, UserCommentAreaMapper> implements UserCommentAreaService {
    @Override
    public Long createDto(BusinessType businessType, Long businessId, @Nullable CommentAreaDto dto) {
        if (dto == null) {
            dto = new CommentAreaDto();
            dto.setCommentFlag(CommentFlag.NORMAL);
            dto.setUserLevel(USER_MIN_LEVEL);
        }
        UserCommentArea entity = BeanUtil.toBean(dto, UserCommentArea.class);
        entity.setId(null);
        entity.setBusiType(businessType);
        entity.setBusiId(businessId);
        save(entity);
        return entity.getId();
    }

    @Override
    public void updateDto(CommentAreaDto dto) {
        UserCommentArea area = getById(dto.getId());
        checkUserPermission(area);
        updateById(BeanUtil.toBean(dto, UserCommentArea.class));
    }

    @Override
    public UserCommentArea getCommentArea(Long businessId) {
        return findOneBy(UserCommentArea::getBusiId, businessId);
    }

    @EventListener(UserCommentEvent.class)
    public void listen(UserCommentEvent e) {
        Long areaId = e.getEntity().getAreaId();
        if (e.getAction() == EventAction.INSERT) {
            Long commentId = e.getEntity().getId();
            updateById(areaId, UserCommentArea::getNewCommentId, commentId);
            lambdaUpdate().set(UserCommentArea::getFstCommentId, commentId)
                    .eq(UserCommentArea::getId, areaId)
                    .eq(UserCommentArea::getFstCommentId, 0)
                    .update(new UserCommentArea());
            incrementById(areaId, UserCommentArea::getComments, 1);
        } else if (e.getAction() == EventAction.DELETE) {
            incrementById(areaId, UserCommentArea::getComments, -1);
        }
    }

    @EventListener(UserCommentTopEvent.class)
    public void listen(UserCommentTopEvent e) {
        updateById(e.getAreaId(), UserCommentArea::getTopCommentId, e.getTop() == 1 ? e.getCommentId() : 0);
    }
}