package pers.tgl.mikufans.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.enums.NoticeType;
import pers.tgl.mikufans.domain.message.MsgUnread;
import pers.tgl.mikufans.domain.system.SysNotice;
import pers.tgl.mikufans.domain.user.UserNotice;
import pers.tgl.mikufans.mapper.UserNoticeMapper;
import pers.tgl.mikufans.service.MsgUnreadService;
import pers.tgl.mikufans.service.UserNoticeService;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.vo.UserNoticeVo;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNoticeServiceImpl extends BaseServiceImpl<UserNotice, UserNoticeMapper> implements UserNoticeService {
    private final MsgUnreadService msgUnreadService;

    @Override
    public void createNotice(NoticeType type, Long userId, String url, String... params) {
        if (type == NoticeType.BROADCAST) {
            throw new RuntimeException("不能发起这种通知");
        }
        UserNotice userNotice = new UserNotice();
        userNotice.setNoticeType(type);
        userNotice.setNoticeId(0L);
        userNotice.setUrl(url);
        if (ArrayUtil.isEmpty(params)) {
            userNotice.setParams(Collections.emptyList());
        } else {
            userNotice.setParams(Arrays.asList(params));
        }
        userNotice.setCreateBy(0L);
        userNotice.setHidden(0);
        userNotice.setReadFlag(0);
        userNotice.setUserId(userId);
        save(userNotice);
        msgUnreadService.setUnreadCount(MsgUnread::getSystems, getUnread(userId), userId);
    }

    @Override
    public IPage<UserNoticeVo> search() {
        createBroadcasts();
        Long contextUserId = SecurityUtils.getContextUserId(true);
        Date date = new Date();
        PageImpl<UserNoticeVo> page = wrapper()
                .selectAll(UserNotice.class)
                .selectAssociation(SysNotice.class, UserNoticeVo::getSysNotice)
                .innerJoin(SysNotice.class, builder->{
                    return builder.eq(SysNotice::getNoticeType, UserNotice::getNoticeType)
                            .ne(SysNotice::getNoticeType, NoticeType.BROADCAST)
                            .or()
                            .eq(SysNotice::getNoticeType, NoticeType.BROADCAST)
                            .eq(SysNotice::getId, UserNotice::getNoticeId);
                })
                .eq(UserNotice::getUserId, contextUserId)
                //启用时间<当前时间<到期时间
                .lt(SysNotice::getEnableTime, date)
                .and(w2 -> {
                    w2.isNull(SysNotice::getExpireTime).or().ge(SysNotice::getExpireTime, date);
                })
                //用户通知显示 或 公告类消息没有设置过
                .and(w3 -> {
                    w3.eq(UserNotice::getHidden, 0).or().isNull(UserNotice::getHidden);
                })
                .orderByAsc(UserNotice::getReadFlag)
                .orderByDesc(UserNotice::getCreateTime)
                .orderByDesc(SysNotice::getEnableTime)
                .page(BaseController.createPage(), UserNoticeVo.class);
        List<Long> reads = new ArrayList<>();
        //注意：如果是公告类型userNotice的内容可能是空的
        page.fill(vo->{
            SysNotice sysNotice = vo.getSysNotice();
            vo.setNoticeType(sysNotice.getNoticeType());
            if (vo.getParams()==null) {
                vo.setParams(Collections.emptyList());
            }
            vo.setReadFlag(0);
            vo.setHidden(0);
            String uri = StrUtil.isBlank(vo.getUrl()) ? sysNotice.getDefUrl() : vo.getUrl();
            String content = StrUtil.indexedFormat(sysNotice.getTemplate(), vo.getParams().toArray())
                    .replace("#uri", uri);
            vo.setTitle(sysNotice.getTitle());
            vo.setUri(uri);
            vo.setContent(content);

            if (vo.getReadFlag() == 1) {
                reads.add(vo.getId());
            }
        });
        if (CollUtil.isNotEmpty(reads)) {
            lambdaUpdate().set(UserNotice::getReadFlag, 1)
                    .in(UserNotice::getId, reads)
                    .update(new UserNotice());
            msgUnreadService.setUnreadCount(MsgUnread::getSystems, getUnread(contextUserId), contextUserId);
        }
        return page;
    }

    /**
     * 自动创建为用户创建公告类消息记录，方便用户设置已读和隐藏
     */
    public void createBroadcasts() {
        Date date = new Date();
        Long contextUserId = SecurityUtils.getContextUserId(true);
        List<UserNotice> userNotices = wrapper()
                .disableLogicDel()
                .selectAll(SysNotice.class)
                //系统通知关联给自己的通知
                .rightJoin(SysNotice.class, builder->{
                    return builder.eq(SysNotice::getId, UserNotice::getNoticeId)
                            .eq(UserNotice::getUserId, contextUserId);
                })
                .eq(SysNotice::getNoticeType, NoticeType.BROADCAST)
                //启用时间<当前时间<到期时间
                .lt(SysNotice::getEnableTime, date)
                .and(w2 -> {
                    w2.isNull(SysNotice::getExpireTime).or().ge(SysNotice::getExpireTime, date);
                })
                .isNull(UserNotice::getId)
                .list(SysNotice.class)
                .stream()
                .map(notice->{
                    UserNotice userNotice = new UserNotice();
                    userNotice.setNoticeType(notice.getNoticeType());
                    userNotice.setNoticeId(notice.getId());
                    userNotice.setUrl("");
                    userNotice.setParams(Collections.emptyList());
                    userNotice.setCreateBy(contextUserId);
                    userNotice.setHidden(0);
                    userNotice.setReadFlag(0);
                    return userNotice;
                })
                .collect(Collectors.toList());
        if (!userNotices.isEmpty()) {
            super.saveBatch(userNotices);
        }
    }

    @Override
    public int getUnread(Long userId) {
        Date date = new Date();
        return wrapper().rightJoin(SysNotice.class, builder->{
                    return builder.eq(SysNotice::getNoticeType, UserNotice::getNoticeType).or()
                            .eq(SysNotice::getNoticeType, NoticeType.BROADCAST);
                })
                //自己的通知或公告
                .and(w->{
                    w.eq(UserNotice::getUserId, userId).or()
                            .eq(SysNotice::getNoticeType, NoticeType.BROADCAST);
                })
                //启用时间<当前时间<到期时间
                .lt(SysNotice::getEnableTime, date)
                .and(w2 -> {
                    w2.isNull(SysNotice::getExpireTime).or().ge(SysNotice::getExpireTime, date);
                })
                //用户通知显示 或 公告类消息没有设置过
                .and(w3 -> {
                    w3.eq(UserNotice::getHidden, 0).or().isNull(UserNotice::getHidden);
                }).count().intValue();
    }

    @Override
    public void setHidden(Long id, int hidden) {
        UserNotice notice = getById(id);
        checkUserPermission(notice);
        updateById(id, UserNotice::getHidden, hidden);
    }
}