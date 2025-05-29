package pers.tgl.mikufans.service.impl;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.comment.UserComment;
import pers.tgl.mikufans.domain.comment.UserCommentArea;
import pers.tgl.mikufans.domain.comment.UserCommentMsg;
import pers.tgl.mikufans.domain.enums.CommentMsgType;
import pers.tgl.mikufans.domain.message.MsgUnread;
import pers.tgl.mikufans.event.EventAction;
import pers.tgl.mikufans.event.UserCommentEvent;
import pers.tgl.mikufans.mapper.UserCommentMsgMapper;
import pers.tgl.mikufans.model.MessageModel;
import pers.tgl.mikufans.service.*;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.vo.UserCommentMsgVo;
import pers.tgl.mikufans.vo.UserCommentVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserCommentMsgServiceImpl extends BaseServiceImpl<UserCommentMsg, UserCommentMsgMapper> implements UserCommentMsgService {
    private final UserCommentService userCommentService;
    private final UserCommentAreaService userCommentAreaService;
    private final MessageService messageService;
    private final MsgUnreadService msgUnreadService;

    @EventListener(UserCommentEvent.class)
    public void listen(UserCommentEvent e) {
        UserComment entity = e.getEntity();
        if (e.getAction() == EventAction.INSERT) {
            UserCommentMsg msg = new UserCommentMsg();
            msg.setMsgType(CommentMsgType.REPLY);
            msg.setCommentId(entity.getId());
            msg.setUserId(entity.getReplyUserId());
            msg.setReadFlag(0);
            if (!Objects.equals(msg.getUserId(), entity.getUserId())) {
                save(msg);
                int unread = getUnread(CommentMsgType.REPLY, msg.getUserId());
                msgUnreadService.setUnreadCount(MsgUnread::getReply, unread, msg.getUserId());
            }
            for (Long atUserId : entity.getAtUsers().values()) {
                UserCommentMsg atMsg = new UserCommentMsg();
                atMsg.setMsgType(CommentMsgType.AT_USER);
                atMsg.setCommentId(entity.getId());
                atMsg.setUserId(atUserId);
                atMsg.setReadFlag(0);
                if (!Objects.equals(atMsg.getUserId(), entity.getUserId())) {
                    save(atMsg);
                    int unread = getUnread(CommentMsgType.AT_USER, atUserId);
                    msgUnreadService.setUnreadCount(MsgUnread::getAtuser, unread, atUserId);
                }
            }
        } else if (e.getAction() == EventAction.DELETE) {
            //删除所有这条评论相关的reply消息和at消息
            lambdaUpdate().eq(UserCommentMsg::getCommentId, entity.getId())
                    .remove();
        }
    }

    @Override
    public PageImpl<UserCommentMsgVo> search(CommentMsgType msgType) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        PageImpl<UserCommentMsgVo> page = wrapper()
                .eq(UserCommentMsg::getMsgType, msgType)
                .eq(UserCommentMsg::getUserId, contextUserId)
                .orderByAsc(UserCommentMsg::getReadFlag)
                .orderByDesc(UserCommentMsg::getCreateTime)
                .page(BaseController.createPage(), UserCommentMsgVo.class);
        List<Long> readIds = new ArrayList<>();
        page.fill(vo->{
            UserCommentVo comment = userCommentService.getVoById(vo.getCommentId());
            if (comment != null) {
                vo.setComment(comment);
                UserCommentArea area = userCommentAreaService.findById(comment.getAreaId());
                vo.setCommentArea(area);
                if (area != null) {
                    MessageModel source = messageService.getModel(area.getBusiType(), area.getBusiId());
                    vo.setSource(source);
                    if (source != null) {
                        vo.setUri(source.getUri() + "#comment" + comment.getId());
                    }
                }
            }
            if (vo.getReadFlag() == 0) {
                readIds.add(vo.getId());
            }
        });
        if (CollUtil.isNotEmpty(readIds)) {
            lambdaUpdate().set(UserCommentMsg::getReadFlag, 1)
                    .in(UserCommentMsg::getId, readIds)
                    .update(new UserCommentMsg());
            int count = getUnread(msgType, contextUserId);
            if (msgType == CommentMsgType.AT_USER) {
                msgUnreadService.setUnreadCount(MsgUnread::getAtuser, count, contextUserId);
            } else if (msgType == CommentMsgType.REPLY) {
                msgUnreadService.setUnreadCount(MsgUnread::getReply, count, contextUserId);
            }
        }
        return page;
    }

    private int getUnread(CommentMsgType msgType, Long userId) {
        return lambdaQuery().eq(UserCommentMsg::getUserId, userId)
                .eq(UserCommentMsg::getMsgType, msgType)
                .eq(UserCommentMsg::getReadFlag, 0)
                .count().intValue();
    }
}