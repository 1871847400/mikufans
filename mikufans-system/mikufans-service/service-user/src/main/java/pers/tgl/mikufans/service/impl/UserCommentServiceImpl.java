package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.domain.comment.UserComment;
import pers.tgl.mikufans.domain.comment.UserCommentArea;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.enums.CommentFlag;
import pers.tgl.mikufans.domain.enums.NoticeType;
import pers.tgl.mikufans.dto.UserCommentDto;
import pers.tgl.mikufans.event.*;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.UserCommentMapper;
import pers.tgl.mikufans.model.MessageModel;
import pers.tgl.mikufans.params.UserCommentParams;
import pers.tgl.mikufans.service.*;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.vo.UserCommentVo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author TGL
 * @createDate 2023-01-15 10:51:58
 */
@Service
@RequiredArgsConstructor
public class UserCommentServiceImpl extends BaseServiceImpl<UserComment, UserCommentMapper> implements UserCommentService {
    private final UserCommentAreaService userCommentAreaService;
    private final UserService userService;
    private final UserLikeDataService userLikeDataService;
    private final UserNoticeService userNoticeService;
    private final MessageService messageService;

    @Override
    public UserCommentVo getVoById(Long id) {
        UserCommentVo vo = getById(id, UserCommentVo.class);
        if (vo != null) {
            fillDefaultProperties(vo);
        }
        return vo;
    }

    @Override
    public PageImpl<UserCommentVo> search(UserCommentParams params) {
        Long areaId = params.getAreaId();
        UserCommentArea area = userCommentAreaService.getById(areaId);
        if (area == null || area.getCommentFlag() == CommentFlag.DISABLED) {
            return PageImpl.empty();
        }
        Long contextUserId = SecurityUtils.getContextUserId(false);
        MPJLambdaWrapper<UserComment> wrapper = wrapper()
                .eqIfExists(UserComment::getAreaId, areaId)
                .eqIfExists(UserComment::getPid, params.getReplyId())
                .gtIfExists(UserComment::getCreateTime, params.getAfterTime())
                .eqIfExists(UserComment::getReplyUserId, params.getReplyUserId())
                .neIfExists(UserComment::getUserId, params.getReplyUserId())
                .eqIfExists(UserComment::getUserId, params.getUserId())
                .and(area.getCommentFlag() == CommentFlag.CHOICE &&
                        !Objects.equals(contextUserId, area.getUserId()), w -> {
                    //如果开启只显示精选评论
                    //1.UP主的评论
                    //2.精选的评论
                    //3.自己的评论
                    //4.当前用户是UP主
                    w.eq(UserComment::getSelected, 1).or()
                            .eq(UserComment::getUserId, area.getUserId()).or()
                            .eq(UserComment::getUserId, contextUserId);
                })
                .orderByDesc(UserComment::getTop)
                .orderByDesc(params.getOrder() == 0, UserComment::getScore)
                .orderByDesc(params.getOrder() == 1, UserComment::getCreateTime);
        PageImpl<UserCommentVo> page = wrapper.page(params.page(), UserCommentVo.class);
        for (UserCommentVo vo : page.getRecords()) {
            fillDefaultProperties(vo);
        }
        if (params.getHots() > 0) {
            page.fill(vo -> {
                if (!vo.isChild()) {
                    List<UserCommentVo> children = wrapper().eq(UserComment::getAreaId, areaId)
                            .eq(UserComment::getPid, vo.getId())
                            .orderByDesc(UserComment::getScore)
                            .last("limit " + params.getHots())
                            .list(UserCommentVo.class);
                    children.forEach(this::fillDefaultProperties);
                    vo.setHots(children);
                }
            });
        }
        return page;
    }

    private void fillDefaultProperties(UserCommentVo vo) {
        vo.setLikeStatus(userLikeDataService.getStatus(vo.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCommentVo create(UserCommentDto dto) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        UserCommentArea area = userCommentAreaService.getById(dto.getAreaId());
        if (area == null || area.getCommentFlag() == CommentFlag.DISABLED) {
            throw new CustomException("无法回复此评论区");
        }
        if (area.getUserLevel() > userService.getUserLevel(contextUserId)) {
            throw new CustomException(Code.LOW_LEVEL);
        }
        Long count = lambdaQuery().eq(UserComment::getAreaId, dto.getAreaId())
                .eq(UserComment::getUserId, contextUserId)
                .count();
        if (count >= 200) {
            throw new CustomException("您无法再发送更多评论了！");
        }
        UserComment comment = BeanUtil.toBean(dto, UserComment.class);
        if (comment.isChild()) {
            //不能回复子评论,防止超过2级评论
            Long pid = getColumnValue(comment.getPid(), UserComment::getPid);
            if (isValidId(pid)) {
                throw new CustomException("不能回复此评论");
            }
            //回复评论增加热度值
            incrementById(comment.getPid(), UserComment::getScore, 10);
            //增加子评论数量
            incrementById(comment.getPid(), UserComment::getChildCount, 1);
        } else {
            //如果不是子评论,则不应该允许回复某条评论
            comment.setReplyId(0L);
            comment.setPid(0L);
        }
        if (comment.getImageIds() == null) {
            comment.setImageIds(Collections.emptyList());
        }
        if (comment.getAtUsers() == null) {
            comment.setAtUsers(MapUtil.empty());
        }
        int uid = wrapper()
                .disableLogicDel()
                .eq(UserComment::getAreaId, comment.getAreaId())
                .eq(UserComment::getPid, comment.getPid())
                .count().intValue() + 1;
        //设置楼层
        comment.setUid(uid);
        //设置回复的用户
        if (isValidId(comment.getReplyId())) {
            Long replyUserId = getColumnValue(comment.getReplyId(), UserComment::getUserId);
            comment.setReplyUserId(replyUserId);
        } else {
            comment.setReplyUserId(area.getUserId());
        }
        //新增评论
        save(comment);
        //创建点赞数据
        userLikeDataService.createLikeData(BusinessType.COMMENT, comment.getId());
        //发布新增评论事件
        publishEvent(new UserCommentEvent(this, comment, EventAction.INSERT));
        return getVoById(comment.getId());
    }

    @Override
    public boolean removeById(UserComment entity) {
        if (super.removeById(entity)) {
            if (entity.isChild()) {
                incrementById(entity.getPid(), UserComment::getChildCount, -1);
                incrementById(entity.getPid(), UserComment::getScore, -10);
            }
            publishEvent(new UserCommentEvent(this, entity, EventAction.DELETE));
            return true;
        }
        return false;
    }

    @Override
    public UserCommentVo getTreeById(Long id) {
        UserCommentVo commentVo = getVoById(id);
        if (commentVo != null && commentVo.isChild()) {
            UserCommentVo parent = getVoById(commentVo.getPid());
            if (parent != null) {
                parent.setHots(Collections.singletonList(commentVo));
                return parent;
            }
        }
        return commentVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTop(Long commentId, int top) {
        UserComment comment = getById(commentId);
        if (comment == null) {
            throw new CustomException("评论不存在");
        }
        if (comment.isChild()) {
            throw new CustomException("子评论无法置顶");
        }
        UserCommentArea area = userCommentAreaService.getById(comment.getAreaId());
        //检查用户是否有评论区的权限
        checkUserPermission(area);
        //首先清除所有置顶
        lambdaUpdate().eq(UserComment::getAreaId, area.getId())
                .set(UserComment::getTop, 0)
                .update(new UserComment());
        if (top == 1) {
            updateById(commentId, UserComment::getTop, top);
        }
        publishEvent(new UserCommentTopEvent(this, area.getId(), commentId, top));
    }

    @Override
    public void setSelected(Long commentId, int selected) {
        Long areaId = getColumnValue(commentId, UserComment::getAreaId);
        UserCommentArea area = userCommentAreaService.getById(areaId);
        checkUserPermission(area);
        updateById(commentId, UserComment::getSelected, selected);
    }

    @EventListener(UserLikeEvent.class)
    public void listen(UserLikeEvent e) {
        if (e.getLikeData().getLikeType() == BusinessType.COMMENT) {
            UserComment comment = getById(e.getLikeData().getBusiId());
            if (comment != null) {
                double score = comment.getChildCount() * 10;
                score += e.getLikeData().getLikes() * 5;
                score += e.getLikeData().getDislikes() * -10;
                updateById(comment.getId(), UserComment::getScore, score);
            }
        }
    }

    @EventListener(UserReportAuditEvent.class)
    public void listen(UserReportAuditEvent e) {
        if (e.getReportType() == BusinessType.COMMENT) {
            UserComment comment = findById(e.getTargetId());
            if (comment == null) {
                return;
            }
            UserCommentArea area = userCommentAreaService.findById(comment.getAreaId());
            if (area == null) {
                return;
            }
            MessageModel model = messageService.getModel(area.getBusiType(), area.getBusiId());
            if (model == null) {
                return;
            }
            NoticeType noticeType = NoticeType.COMMENT_REPORT_FAIL;
            if (e.getAuditStatus() == AuditStatus.SUCCESS) {
                noticeType = NoticeType.COMMENT_REPORT_SUCESS;
                removeById(comment);
                userNoticeService.createNotice(NoticeType.COMMENT_VIOLATION, comment.getUserId(), model.getUri(),
                        model.getMessage(), comment.getContent(), e.getMessage());
            }
            for (Long userId : e.getUserIds()) {
                userNoticeService.createNotice(noticeType, userId, model.getUri(),
                        model.getMessage(), comment.getContent());
            }
        }
    }
}