package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.message.UserWhisper;
import pers.tgl.mikufans.dto.UserWhisperDto;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.UserWhisperMapper;
import pers.tgl.mikufans.search.UserWhisperSearch;
import pers.tgl.mikufans.service.UserDialogService;
import pers.tgl.mikufans.service.UserWhisperService;
import pers.tgl.mikufans.socket.MsgSocketListener;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserWhisperServiceImpl extends BaseServiceImpl<UserWhisper, UserWhisperMapper> implements UserWhisperService {
    private final UserDialogService userDialogService;
    private final MsgSocketListener msgSocketListener;

    @Override
    public long getWhisperCount(@Nullable Long targetId, @Nullable Integer read) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        return wrapper().eq(UserWhisper::getReceiverId, contextUserId)
                .eqIfExists(UserWhisper::getUserId, targetId)
                .eqIfExists(UserWhisper::getReadFlag, read)
                .count();
    }

    @Override
    public Page<UserWhisper> search(UserWhisperSearch params) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        PageImpl<UserWhisper> page = wrapper()
                .eqIfExists(UserWhisper::getReadFlag, params.getReadFlag())
                .and(wrapper -> {
                    wrapper.eq(UserWhisper::getUserId, contextUserId)
                            .eq(UserWhisper::getReceiverId, params.getTargetId())
                            .or()
                            .eq(UserWhisper::getUserId, params.getTargetId())
                            .eq(UserWhisper::getReceiverId, contextUserId);
                })
                .orderByDesc(UserWhisper::getCreateTime)
                .page(params.page());
        List<Long> readIds = new ArrayList<>();
        for (UserWhisper record : page.getRecords()) {
            if (record.getReadFlag() == 0 && Objects.equals(record.getReceiverId(), contextUserId)) {
                readIds.add(record.getId());
            }
        }
        if (CollUtil.isNotEmpty(readIds)) {
            lambdaUpdate().set(UserWhisper::getReadFlag, 1)
                    .in(UserWhisper::getId, readIds)
                    .update(new UserWhisper());
            userDialogService.onReadWhisper(params.getTargetId(), readIds.size());
        }
        return page;
    }

    @Override
    public UserWhisper createWhisper(UserWhisperDto dto) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        if (Objects.equals(dto.getReceiverId(), contextUserId)) {
            throw new CustomException("不能给自己发送私信");
        }
        UserWhisper userWhisper = BeanUtil.toBean(dto, UserWhisper.class);
        userWhisper.setReadFlag(0);
        userWhisper.setRevoked(0);
        save(userWhisper);
        userDialogService.createDialog(userWhisper.getReceiverId());
        userDialogService.onNewWhisper(userWhisper);
        //向对话双方发送新消息事件
        msgSocketListener.sendEvent("new_whisper", userWhisper.getReceiverId(), userWhisper);
        msgSocketListener.sendEvent("new_whisper", userWhisper.getUserId(), userWhisper);
        return userWhisper;
    }

    @Override
    public void revokeWhisper(Long id) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        UserWhisper userWhisper = lambdaQuery()
                .eq(UserWhisper::getId, id)
                .eq(UserWhisper::getUserId, contextUserId)
                .eq(UserWhisper::getRevoked, 0)
                .one();
        if (userWhisper == null) {
            throw new CustomException("私信不存在或已被撤回");
        }
        long limit = Duration.ofMinutes(2).toMillis();
        if (System.currentTimeMillis() - userWhisper.getCreateTime().getTime() > limit) {
            throw new CustomException("只能撤回两分钟内的消息");
        }
        lambdaUpdate().eq(UserWhisper::getId, id)
                .set(UserWhisper::getRevoked, 1)
                .update(new UserWhisper());
        //向对话双方发送撤回事件
        msgSocketListener.sendEvent("revoke_whisper", userWhisper.getReceiverId(), userWhisper.getId());
        msgSocketListener.sendEvent("revoke_whisper", userWhisper.getUserId(), userWhisper.getId());
    }
}