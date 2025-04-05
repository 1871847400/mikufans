package pers.tgl.mikufans.controller.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.config.AppConfig;

@Component
@RequiredArgsConstructor
public class TencentApi implements OAuthApi {
    private final AppConfig appConfig;
    private static final String TAG = "tencent";

    @Override
    public String getAuthType() {
        return TAG;
    }

    @Override
    public String getAuthCodeUrl() {
        return "";
    }

    @Override
    public String getAccessToken(String code) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OAuthUser getOAuthUser(String accessToken) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}