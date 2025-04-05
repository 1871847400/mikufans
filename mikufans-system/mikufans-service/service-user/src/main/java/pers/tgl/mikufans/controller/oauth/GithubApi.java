package pers.tgl.mikufans.controller.oauth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.config.AppConfig;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GithubApi implements OAuthApi {
    private final AppConfig appConfig;
    private final static String TAG = "github";

    /**
     * 授权码页面
     */
    private final String GET_CODE_URL = "https://github.com/login/oauth/authorize";
    /**
     * 根据授权码获取TOKEN的URL
     */
    private final String GET_TOKEN_URL = "https://github.com/login/oauth/access_token";
    /**
     * 根据TOKEN获取用户信息的URL
     */
    private final String GET_USER_INFO_URL = "https://api.github.com/user";
    /**
     * 获取邮箱的接口地址
     */
    private final String GET_EMAIL_URL = "https://api.github.com/user/emails";

    @Override
    public String getAuthType() {
        return TAG;
    }

    @Override
    public String getAuthCodeUrl() {
        Map<String, String> params = appConfig.getOauth().get(TAG);
        return UrlBuilder.of(GET_CODE_URL)
                .addQuery("client_id", params.get("client_id"))
                .addQuery("redirect_uri", params.get("redirect_uri"))
                .addQuery("scope", "user:email")
                .build();
    }

    @Override
    public String getAccessToken(String code) {
        Map<String, String> params = appConfig.getOauth().get(TAG);
        UrlBuilder builder = UrlBuilder.of(GET_TOKEN_URL)
                .addQuery("code", code);
        for (Map.Entry<String, String> e : params.entrySet()) {
            builder.addQuery(e.getKey(), e.getValue());
        }
        String body = HttpUtil.get(builder.build());
        Map<String, List<String>> result = HttpUtil.decodeParams(body, StandardCharsets.UTF_8);
        List<String> accessToken = result.get("access_token");
        if (CollUtil.isEmpty(accessToken)) {
            throw new RuntimeException("token is empty");
        }
        return accessToken.get(0);
    }


    @Override
    public OAuthUser getOAuthUser(String accessToken) {
        HttpResponse response = HttpUtil.createGet(GET_USER_INFO_URL)
                .header(Header.AUTHORIZATION, "Bearer " + accessToken)
                .execute();
        JSONObject result = new JSONObject(response.body());
        response.close();
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setUsername(result.optString("login"));
        oAuthUser.setNickname(result.optString("name"));
        oAuthUser.setAvatarUrl(result.optString("avatar_url"));
        String email = result.optString("email");
        if (StrUtil.isBlank(email)) {
            email = getFirstVerifiedEmail(accessToken);
        }
        oAuthUser.setEmail(email);
        return oAuthUser;
    }

    /**
     * github的邮箱需要专门调用接口获取
     * 使用条件：
     *      1.用户必须公开邮箱
     *      2.授权地址必须包含: scope=user:email
     * 响应格式:
     *      [{ email: "xxx", primary: true, verified: true, visibility: "public" }]
     */
    private String getFirstVerifiedEmail(String accessToken) {
        HttpResponse response = HttpUtil.createGet(GET_EMAIL_URL)
                .header(Header.AUTHORIZATION, "Bearer " + accessToken)
                .header(Header.ACCEPT, "application/vnd.github+json")
                .execute();
        String body = response.body();
        response.close();
        if (!JSONUtil.isTypeJSONArray(body)) {
            return "";
        }
        JSONArray array = new JSONArray(body);
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            boolean verified = jsonObject.optBoolean("verified", false);
            if (verified) {
                return jsonObject.optString("email");
            }
        }
        return "";
    }
}