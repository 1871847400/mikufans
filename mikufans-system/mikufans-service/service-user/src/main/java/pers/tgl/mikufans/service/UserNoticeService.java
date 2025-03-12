package pers.tgl.mikufans.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.tgl.mikufans.domain.enums.NoticeType;
import pers.tgl.mikufans.domain.user.UserNotice;
import pers.tgl.mikufans.vo.UserNoticeVo;

public interface UserNoticeService extends BaseService<UserNotice> {
    /**
     * 创建通知
     */
    void createNotice(NoticeType type, Long userId, String url, String... params);
    /**
     * 搜索与自己相关的通知
     */
    IPage<UserNoticeVo> search();
    /**
     * 未读通知数量
     */
    int getUnread(Long userId);
    /**
     * 隐藏通知
     */
    void setHidden(Long id, int hidden);
}