package pers.tgl.mikufans.service;

import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.comment.UserCommentArea;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.dto.CommentAreaDto;

public interface UserCommentAreaService extends BaseService<UserCommentArea> {
    /**
     * 创建评论区
     */
    Long createDto(BusinessType businessType, Long businessId, @Nullable CommentAreaDto dto);
    /**
     * 更新评论区
     */
    void updateDto(CommentAreaDto dto);
    /**
     * 根据业务id获取评论区
     */
    UserCommentArea getCommentArea(Long businessId);
}