package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.bangumi.Bangumi;
import pers.tgl.mikufans.domain.user.UserRate;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserRateVo extends UserRate {
    /**
     * 动态
     */
    private UserDynamicVo dynamic;
    /**
     * 节目数据
     */
    private Bangumi bangumi;
    /**
     * 点赞数据
     */
    public LikeStatus getLikeStatus() {
        return dynamic != null ? dynamic.getLikeStatus() : null;
    }
}