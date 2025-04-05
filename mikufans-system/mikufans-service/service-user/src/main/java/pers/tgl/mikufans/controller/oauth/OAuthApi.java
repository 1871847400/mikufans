package pers.tgl.mikufans.controller.oauth;

public interface OAuthApi {
    /**
     * 登录方式名称
     */
    String getAuthType();

    /**
     * 生成授权码地址
     * 用户在前端使用这个地址访问授权页面，同意后会跳转到指定的地址，会携带参数：ip?code=授权码
     * 前端调用后端的授权码登录API
     * 后端使用授权码去获取token，再用token拿到用户信息
     */
    String getAuthCodeUrl();

    /**
     * 根据用户拿到的授权码获取TOKEN （用授权码换token）
     * @param code 授权码
     */
    String getAccessToken(String code);

    /**
     * 根据accessToken获取用户详细信息 (用token换用户信息)
     * @param accessToken getAccessToken()的返回值
     */
    OAuthUser getOAuthUser(String accessToken);
}