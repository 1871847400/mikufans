package pers.tgl.mikufans.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.message.MsgUnread;

public interface MsgUnreadService extends BaseService<MsgUnread> {
    /**
     * 获取用户未读消息数,如果没有会返回默认的
     */
    MsgUnread getByUser(@Nullable Long userId);
    /**
     * 设置消息数量
     */
    void setUnreadCount(SFunction<MsgUnread, Integer> col, int count, Long userId);
}