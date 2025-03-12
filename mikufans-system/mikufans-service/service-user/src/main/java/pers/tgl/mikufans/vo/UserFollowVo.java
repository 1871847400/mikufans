package pers.tgl.mikufans.vo;

import lombok.Data;
import pers.tgl.mikufans.consts.UserFollowStatus;

import java.io.Serializable;

@Data
public class UserFollowVo implements Serializable {
    private Long id;
    private String nickname;
    private String sign;
    private Long avatarId;
    private Integer level;
    private Integer fans;
    /**
     * 高亮后的昵称 (如果用了关键字搜索)
     */
    private String highlighted;
    /**
     * 自己当前用户的关注状态 (可能是用户A查看用户B的关注列表)
     */
    private UserFollowStatus follow;
    /**
     * 是否有最新动态，用于动态页头像上显示小点
     */
    private Boolean news;

    public String getUri() {
        return "/space/" + id;
    }
}