package pers.tgl.mikufans.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@PermFlag(name = "用户点赞", group = PermGroup.BUSINESS)
public class UserLike extends UserBaseEntity {
    public static final Integer LIKE_VAL = 1;
    public static final Integer DISLIKE_VAL = 2;
    /**
     * 点赞数据id
     */
    private Long likeDataId;
    /**
     * 点赞值
     */
    private Integer likeVal;
    /**
     * 点赞时间
     */
    @JsonFormat(pattern = "yy-MM-dd HH:mm")
    public Date getLikeTime() {
        return getCreateTime();
    }
}