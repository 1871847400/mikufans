package pers.tgl.mikufans.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.message.MsgUnread;
import pers.tgl.mikufans.domain.message.UserDialog;
import pers.tgl.mikufans.domain.message.UserWhisper;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.UserDialogMapper;
import pers.tgl.mikufans.service.MsgUnreadService;
import pers.tgl.mikufans.service.UserDialogService;
import pers.tgl.mikufans.service.UserService;
import pers.tgl.mikufans.socket.MsgSocketListener;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserDialogServiceImpl extends BaseServiceImpl<UserDialog, UserDialogMapper> implements UserDialogService {
    private final MsgSocketListener msgSocketListener;
    private final UserService userService;
    private final MsgUnreadService msgUnreadService;

    @Override
    public void createDialog(Long targetId) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        if (Objects.equals(contextUserId, targetId)) {
            throw new CustomException("不能和自己私信");
        }
        createDialog(contextUserId, targetId);
        createDialog(targetId, contextUserId);
    }

    private void createDialog(long userId, long targetId) {
        UserDialog userDialog = lambdaQuery()
                .eq(UserDialog::getTargetId, targetId)
                .eq(UserDialog::getUserId, userId)
                .one();
        if (userDialog == null) {
            userDialog = new UserDialog();
            userDialog.setUserId(userId);
            userDialog.setTargetId(targetId);
            userDialog.setTop(0);
            save(userDialog);
            msgSocketListener.sendEvent("new_dialog", userId, userDialog);
        } else {
            //设置最近修改时间,使其位置上升
            lambdaUpdate().eq(UserDialog::getId, userDialog.getId())
                    .update(new UserDialog());
        }
    }

    @Override
    public UserDialog createTempDialog(Long targetId) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        UserDialog userDialog = lambdaQuery()
                .eq(UserDialog::getTargetId, targetId)
                .eq(UserDialog::getUserId, contextUserId)
                .one();
        if (userDialog == null) {
            if (!userService.exists(targetId)) {
                return null;
            }
            userDialog = new UserDialog();
            userDialog.setId(0L);//表示临时会话
            userDialog.setTargetId(targetId);
            userDialog.setUserId(contextUserId);
            userDialog.setTop(0);
            userDialog.setUnread(0);
            userDialog.setLastId(0L);
        }
        return userDialog;
    }

    @Override
    public PageImpl<UserDialog> search() {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        return wrapper()
                .eq(UserDialog::getUserId, contextUserId)
                .orderByDesc(UserDialog::getTop, UserDialog::getUpdateTime)
                .page(BaseController.createPage());
    }

    @Override
    public void setTop(Long id, boolean top) {
        //这里不需要触发自动填充
        Long contextUserId = SecurityUtils.getContextUserId(true);
        lambdaUpdate().eq(UserDialog::getUserId, contextUserId)
                .eq(UserDialog::getTop, 1)
                .set(UserDialog::getTop, 0)
                .update();
        if (top) {
            lambdaUpdate().eq(UserDialog::getUserId, contextUserId)
                    .eq(UserDialog::getId, id)
                    .set(UserDialog::getTop, 1)
                    .update();
        }
    }

    @Override
    public int getUnread(@Nullable Long userId) {
        if (userId == null) {
            userId = SecurityUtils.getContextUserId(true);
        }
        UserDialog userDialog = wrapper().selectSum(UserDialog::getUnread)
                .eq(UserDialog::getUserId, userId)
                .one();
        return userDialog == null ? 0 : userDialog.getUnread();
    }

    @Override
    public void onNewWhisper(UserWhisper userWhisper) {
        UserDialog userDialog = lambdaQuery().eq(UserDialog::getTargetId, userWhisper.getUserId())
                .eq(UserDialog::getUserId, userWhisper.getReceiverId())
                .one();
        if (userDialog != null) {
            //增加对方的未读数
            incrementById(userDialog.getId(), UserDialog::getUnread, 1);
            //设置双方的最后一条消息
            lambdaUpdate().set(UserDialog::getLastId, userWhisper.getId())
                    .eq(UserDialog::getId, userDialog.getId())
                    .or()
                    .eq(UserDialog::getUserId, userWhisper.getUserId())
                    .eq(UserDialog::getTargetId, userWhisper.getReceiverId())
                    .update(new UserDialog());
            msgSocketListener.sendEvent("new_dialog", userDialog.getUserId(), getById(userDialog.getId()));
            msgUnreadService.setUnreadCount(MsgUnread::getWhisper, getUnread(userDialog.getUserId()), userDialog.getUserId());
        }
    }

    @Override
    public void onReadWhisper(Long targetId, int count) {
        UserDialog targetDialog = wrapper()
                .eq(UserDialog::getUserId, SecurityUtils.getContextUserId(true))
                .eq(UserDialog::getTargetId, targetId)
                .one();
        if (targetDialog != null) {
            incrementById(targetDialog.getId(), UserDialog::getUnread, -count);
            msgSocketListener.sendEvent("new_dialog", targetDialog.getUserId(), getById(targetDialog.getId()));
            msgUnreadService.setUnreadCount(MsgUnread::getWhisper, getUnread(targetDialog.getUserId()), targetDialog.getUserId());
        }
    }
}