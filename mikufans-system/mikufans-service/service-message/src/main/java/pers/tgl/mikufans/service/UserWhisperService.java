package pers.tgl.mikufans.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.message.UserWhisper;
import pers.tgl.mikufans.dto.UserWhisperDto;
import pers.tgl.mikufans.search.UserWhisperSearch;

public interface UserWhisperService extends BaseService<UserWhisper> {
    /**
     *
     */
    Page<UserWhisper> search(UserWhisperSearch search);
    /**
     *
     */
    UserWhisper createWhisper(UserWhisperDto dto);
    /**
     * 撤销发送的私信,并且设置状态为未读
     * 注意撤回应该有时间限制
     * @param id 私信id
     */
    void revokeWhisper(Long id);
    /**
     * 计算当前用户有多少对方的消息
     * @param targetId 限制对象
     * @param read 0表示未读消息 1表示已读消息 null表示都计算
     */
    long getWhisperCount(@Nullable Long targetId, @Nullable Integer read);
}