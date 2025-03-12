package pers.tgl.mikufans.domain.message;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.user.User;

/**
 * 用户私信会话表
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserDialog extends UserBaseEntity {
    /**
     * 目标用户id
     */
    private Long targetId;
    /**
     * 是否置顶
     */
    private Integer top;
    /**
     * 最后一条消息id
     */
    private Long lastId;
    /**
     * 未读消息数量
     */
    private Integer unread;
    /**
     * 最后一条消息
     */
    @Nullable
    public UserWhisper getLastWhisper() {
        return getLastId() != null && getLastId() != 0 ? Db.getById(lastId, UserWhisper.class) : null;
    }
    /**
     * 聊天对象的用户数据
     */
    public User getTarget() {
        return getUserBase(getTargetId());
    }
}