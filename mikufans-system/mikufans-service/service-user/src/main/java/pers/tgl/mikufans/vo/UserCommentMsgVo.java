package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.comment.UserCommentArea;
import pers.tgl.mikufans.domain.comment.UserCommentMsg;
import pers.tgl.mikufans.model.MessageModel;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserCommentMsgVo extends UserCommentMsg {
    /**
     * 评论本身
     */
    private UserCommentVo comment;
    /**
     * 评论区
     * UserCommentVo用不到评论区属性,消息列表中才有用
     */
    private UserCommentArea commentArea;
    /**
     * 消息源
     */
    private MessageModel source;
    /**
     * 评论的访问地址
     */
    private String uri;
}