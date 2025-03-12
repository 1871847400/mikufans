package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.domain.user.UserLikeData;
import pers.tgl.mikufans.model.MessageModel;
import pers.tgl.mikufans.util.MyUtils;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserLikeDataVo extends UserLikeData {
    /**
     * 消息来源
     */
    private MessageModel source;
    /**
     * 当前用户点赞值
     */
    private Integer likeVal;
    /**
     * 点赞的业务名称
     */
    private String likeLabel;
    /**
     * 点赞的用户列表
     */
    public List<User> getLikeUsers() {
        return getLikeUserIds()
                .stream()
                .unordered()
                .map(UserBaseEntity::getUserBase)
                .collect(Collectors.toList());
    }
    /**
     * 点赞时间格式化
     */
    public String getLikeTimeStr() {
        return MyUtils.formatHumanTime(getLikeTime());
    }
}