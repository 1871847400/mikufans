package pers.tgl.mikufans.controller.oauth;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.config.AppConfig;

import java.util.Map;

/**
 * 注意需要服务器能够连接google
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleApi implements OAuthApi {
    private static final String TAG = "google";

    private final AppConfig appConfig;

    @Override
    public String getAuthType() {
        return TAG;
    }

    @Override
    public String getAuthCodeUrl() {
        Map<String, String> params = appConfig.getOauth().get(TAG);
        return UrlBuilder.of("https://accounts.google.com/o/oauth2/v2/auth")
                .addQuery("client_id", params.get("client_id"))
                .addQuery("redirect_uri", params.get("redirect_uri"))
                .addQuery("response_type", "code")
                .addQuery("scope", "openid email profile")
                .addQuery("access_type", "offline")
                .build();
    }

    @Override
    public String getAccessToken(String code) {
        Map<String, Object> params = BeanUtil.copyProperties(appConfig.getOauth().get(TAG), Map.class);
        params.put("code", code);
        String body = HttpUtil.post("https://oauth2.googleapis.com/token", params, 20000);
        JSONObject result = new JSONObject(body);
        return result.getString("access_token");
    }

    @Override
    public OAuthUser getOAuthUser(String accessToken) {
        HttpResponse response = HttpUtil.createPost("https://www.googleapis.com/oauth2/v3/userinfo")
                .header(Header.AUTHORIZATION, "Bearer " + accessToken)
                .execute();
        JSONObject result = new JSONObject(response.body());
        response.close();
        OAuthUser oAuthUser = new OAuthUser();
        // sub = google唯一用户id
        oAuthUser.setUsername(result.optString("sub"));
        oAuthUser.setNickname(result.optString("name"));
        oAuthUser.setEmail(result.optString("email"));
        oAuthUser.setAvatarUrl(result.optString("picture"));
        return oAuthUser;
    }
}