package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.enums.CommentMsgType;
import pers.tgl.mikufans.domain.comment.UserCommentMsg;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.vo.UserCommentMsgVo;

public interface UserCommentMsgService extends BaseService<UserCommentMsg> {
    /**
     * 搜索评论的消息列表
     */
    PageImpl<UserCommentMsgVo> search(CommentMsgType msgType);
}