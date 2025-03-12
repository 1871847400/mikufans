package pers.tgl.mikufans.domain.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

/**
 * 用户收藏夹表
 * @TableName user_star
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="user_star")
@Data
@FieldNameConstants
public class UserStar extends UserBaseEntity {
    /**
     * 收藏夹名称
     */
    private String starName;
    /**
     * 收藏数量
     */
    private Integer starCount;
    /**
     * 是否默认收藏夹 0否1是
     */
    private Integer defFlag;
    /**
     * 收藏夹封面id
     */
    private Long coverId;
    /**
     * 收藏夹简介
     */
    private String intro;
    /**
     * 是否公开
     */
    private Integer visible;
    /**
     * 排序
     */
    private Integer sort;

    public String getStarDesc() {
        return visible == 0 ? "私密" : "公开";
    }

    public String getUri() {
        return "/space/" + getUserId() + "/star" + (getDefFlag()==1 ? "" : "#" + getId());
    }
}