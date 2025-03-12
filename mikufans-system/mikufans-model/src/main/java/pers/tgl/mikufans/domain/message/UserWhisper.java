package pers.tgl.mikufans.domain.message;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
public class UserWhisper extends UserBaseEntity {
    /**
     * 私信内容  文字或图片id
     */
    private String message;
    /**
     * 内容类型 1文字 2图片
     */
    private Integer msgType;
    /**
     * 是否已读
     */
    private Integer readFlag;
    /**
     * 是否撤回 0否 1撤回
     */
    private Integer revoked;
    /**
     * 接收用户id
     */
    private Long receiverId;
    /**
     * 私信发送时间
     */
    private Date getSendTime() {
        return getCreateTime();
    }
    /**
     * 如果消息被撤回了则应该返回空
     */
    public String getMessage() {
        return ObjectUtil.equals(getRevoked(), 1) ? "" : message;
    }
}