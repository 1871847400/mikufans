package pers.tgl.mikufans.service;

import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.message.UserDialog;
import pers.tgl.mikufans.domain.message.UserWhisper;
import pers.tgl.mikufans.util.PageImpl;

public interface UserDialogService extends BaseService<UserDialog> {
    /**
     * 创建和指定用户的会话,如果已存在则直接返回
     */
    void createDialog(Long targetId);
    /**
     * 创建和用户的临时会话,如果已存在则返回
     */
    UserDialog createTempDialog(Long targetId);
    /**
     * 获取用户最近的所有会话
     */
    PageImpl<UserDialog> search();
    /**
     * 置顶
     */
    void setTop(Long id, boolean top);
    /**
     * 获取未读消息总数
     */
    int getUnread(@Nullable Long userId);

    void onNewWhisper(UserWhisper userWhisper);
    void onReadWhisper(Long targetId, int count);
}