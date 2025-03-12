package pers.tgl.mikufans.service.impl;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.message.MsgUnread;
import pers.tgl.mikufans.mapper.MsgUnreadMapper;
import pers.tgl.mikufans.service.MsgUnreadService;
import pers.tgl.mikufans.socket.MsgSocketListener;
import pers.tgl.mikufans.util.SecurityUtils;

@Service
@RequiredArgsConstructor
public class MsgUnreadServiceImpl extends BaseServiceImpl<MsgUnread, MsgUnreadMapper> implements MsgUnreadService {
    private static final String SOCKET_MSG_UNREAD_EVT = "msg_unread";
    private final MsgSocketListener msgSocketListener;

    @Override
    public void setUnreadCount(SFunction<MsgUnread, Integer> col, int count, Long userId) {
        String methodName = LambdaUtils.extract(col).getImplMethodName();
        synchronized (("msg_unread:" + userId).intern()) {
            MsgUnread msgUnread = getByUser(userId);
            ReflectUtil.invoke(msgUnread, methodName.replaceFirst("get", "set"), count);
            saveOrUpdate(msgUnread);
            msgSocketListener.sendEvent(SOCKET_MSG_UNREAD_EVT, userId, msgUnread);
        }
    }

    @Override
    public MsgUnread getByUser(@Nullable Long userId) {
        if (userId == null) {
            userId = SecurityUtils.getContextUserId(true);
        }
        MsgUnread msgUnread = getOneBy(MsgUnread::getUserId, userId);
        if (msgUnread == null) {
            msgUnread = MsgUnread.createDefault();
            msgUnread.setUserId(userId);
        }
        return msgUnread;
    }
}