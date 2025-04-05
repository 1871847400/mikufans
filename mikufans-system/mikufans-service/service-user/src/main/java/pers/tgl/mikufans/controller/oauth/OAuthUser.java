package pers.tgl.mikufans.controller.oauth;

import lombok.Data;

/**
 * 统一封装OAUTH用户
 */
@Data
public class OAuthUser {
    private String username;
    private String nickname;
    private String avatarUrl;
    private String email;
}
