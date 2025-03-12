package pers.tgl.mikufans.domain.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

/**
 * 未读消息数量
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MsgUnread extends UserBaseEntity {
    private Integer whisper;
    private Integer reply;
    private Integer atuser;
    private Integer likes;
    private Integer systems;

    public int getTotal() {
        int total = 0;
        if (whisper != null) {
            total += whisper;
        }
        if (reply != null) {
            total += reply;
        }
        if (atuser != null) {
            total += atuser;
        }
        if (likes != null) {
            total += likes;
        }
        if (systems != null) {
            total += systems;
        }
        return total;
    }

    public static MsgUnread createDefault() {
        MsgUnread msgUnread = new MsgUnread();
        msgUnread.whisper = 0;
        msgUnread.reply = 0;
        msgUnread.atuser = 0;
        msgUnread.likes = 0;
        msgUnread.systems = 0;
        return msgUnread;
    }
}