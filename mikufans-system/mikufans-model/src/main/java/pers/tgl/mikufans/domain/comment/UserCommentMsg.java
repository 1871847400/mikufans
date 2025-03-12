package pers.tgl.mikufans.domain.comment;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.enums.CommentMsgType;
import pers.tgl.mikufans.util.MyUtils;

@EqualsAndHashCode(callSuper = true)
@Data
@PermFlag(name = "评论消息", group = PermGroup.BUSINESS)
@TableName(autoResultMap = true)
public class UserCommentMsg extends UserBaseEntity {
    /**
     * 评论id
     */
    private Long commentId;
    /**
     * 消息类型
     */
    private CommentMsgType msgType;
    /**
     * 是否已读
     */
    private Integer readFlag;
    /**
     * 发布时间
     */
    public String getPublishTime() {
        return MyUtils.formatHumanTime(getCreateTime());
    }
}