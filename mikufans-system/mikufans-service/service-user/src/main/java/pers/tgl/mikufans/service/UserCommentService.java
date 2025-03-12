package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.comment.UserComment;
import pers.tgl.mikufans.dto.UserCommentDto;
import pers.tgl.mikufans.params.UserCommentParams;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.vo.UserCommentVo;

/**
* @author TGL
* @createDate 2023-01-15 10:51:58
*/
public interface UserCommentService extends BaseService<UserComment> {
    /**
     * 查询指定评论的信息
     */
    UserCommentVo getVoById(Long id);
    /**
     * 根据条件分页查找评论
     */
    PageImpl<UserCommentVo> search(UserCommentParams search);
    /**
     * 用户发送评论
     */
    UserCommentVo create(UserCommentDto dto);
    /**
     * 如果commentId是父评论则和getById无区别
     * 如果commentId是子评论则返回父评论顺带目标子评论
     */
    UserCommentVo getTreeById(Long id);
    /**
     * 设置评论置顶
     */
    void setTop(Long commentId, int top);
    /**
     * 设置精选评论
     */
    void setSelected(Long commentId, int selected);
}