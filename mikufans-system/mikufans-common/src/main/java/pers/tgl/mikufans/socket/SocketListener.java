package pers.tgl.mikufans.socket;

import cn.hutool.core.map.BiMap;
import cn.hutool.extra.spring.SpringUtil;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.consts.TokenType;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.util.JsonUtils;
import pers.tgl.mikufans.util.JwtUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public interface SocketListener extends ConnectListener, DisconnectListener, AuthTokenListener {
    public static final Logger log = LoggerFactory.getLogger(SocketListener.class);

    /**
     * 双向map,存储 sessionId 和 用户id
     */
    BiMap<String, Long> USER_INFOS = new BiMap<>(new ConcurrentHashMap<>());

    /**
     * 命名空间名称,注入容器后会自动进行注册
     */
    String getNamespace();

    /**
     * 是否需要登录后才能创建连接
     */
    default boolean forceLogin() {
        return false;
    }

    default SocketIONamespace getNamespaceServer() {
        return SpringUtil.getBean(SocketIOServer.class).getNamespace(getNamespace());
    }

    default void onConnect(SocketIOClient client) {
        log.info("socket connect sessionId={} ip={}", client.getSessionId(), client.getRemoteAddress());
    }

    default void onDisconnect(SocketIOClient client) {
        String sessionId = client.getSessionId().toString();
        USER_INFOS.remove(sessionId);
    }

    @Nullable
    default Long getUserId(SocketIOClient client) {
        String sessionId = client.getSessionId().toString();
        return USER_INFOS.get(sessionId);
    }

    @Nullable
    default String getSessionId(Long userId) {
        return USER_INFOS.getKey(userId);
    }

    @Nullable
    default SocketIOClient getClient(Long userId) {
        String sessionId = getSessionId(userId);
        return sessionId != null ? getNamespaceServer().getClient(UUID.fromString(sessionId)) : null;
    }

    default void sendEvent(String event, Long userId, Object... data) {
        SocketIOClient client = getClient(userId);
        if (client != null && data != null && data.length > 0) {
            ObjectMapper objectMapper = SpringUtil.getBean(ObjectMapper.class);
            for (int i = 0; i < data.length; i++) {
                try {
                    data[i] = objectMapper.writeValueAsString(data[i]);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return;
                }
            }
            client.sendEvent(event, data);
            log.info("socket send ip: {} data: {}", client.getRemoteAddress(), data);
        }
    }

    /**
     * 会在OnConnect之前触发
     * 如果返回失败的结果,则不会进入OnConnect而是onDisconnect
     */
    default AuthTokenResult getAuthTokenResult(Object authToken, SocketIOClient client) {
        String sessionId = client.getSessionId().toString();
        boolean isLogin = USER_INFOS.containsKey(sessionId);
        if (!isLogin && authToken instanceof Map) {
            Map<String, Object> authTokenMap = (Map<String, Object>) authToken;
            Object token = authTokenMap.get("token");
            if (token instanceof String) {
                AppConfig appConfig = SpringUtil.getBean(AppConfig.class);
                String secret = appConfig.getToken().get(TokenType.REFRESH).getSecret();
                try {
                    String subject = JwtUtils.parseJwt(token.toString(), secret).getSubject();
                    UserToken userToken = JsonUtils.read(subject, UserToken.class);
                    if (userToken.getUserType() != 0) {
                        throw new CustomException(Code.FORBIDDEN);
                    }
                    USER_INFOS.put(sessionId, userToken.getId());
                    isLogin = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (forceLogin() && !isLogin) {
            return new AuthTokenResult(false, "未登录");
        }
        return AuthTokenResult.AuthTokenResultSuccess;
    }
}