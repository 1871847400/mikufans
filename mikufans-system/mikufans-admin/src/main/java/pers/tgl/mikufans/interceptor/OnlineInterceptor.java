package pers.tgl.mikufans.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pers.tgl.mikufans.entity.OnlineUser;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.util.IpUtils;
import pers.tgl.mikufans.util.RedisUtils;
import pers.tgl.mikufans.util.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OnlineInterceptor implements HandlerInterceptor {
    private static final String redisKeyPrefix = "online_user:";
    private static final Duration duration = Duration.ofSeconds(60);

    private final ThreadPoolTaskExecutor taskExecutor;
    private final RedisUtils redisUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserToken userToken = SecurityUtils.getContextUserToken();
        if (userToken != null) {
            taskExecutor.submit(()->handle(userToken, request));
        }
        return true;
    }

    private void handle(UserToken userToken, HttpServletRequest request) {
        String redisKey = redisKeyPrefix + userToken.getUserType() + ":" + userToken.getId();
        boolean exists = redisUtils.exists(redisKey);
        if (!exists) {
            OnlineUser onlineUser = new OnlineUser();
            onlineUser.setId(userToken.getId());
            onlineUser.setUsername(userToken.getUsername());
            onlineUser.setUserType(userToken.getUserType());
            onlineUser.setIpaddr(IpUtils.getIpaddr(request));
            onlineUser.setLocation(IpUtils.getRealLocation(onlineUser.getIpaddr()));
            onlineUser.setTime(new Date());
            redisUtils.set(redisKey, onlineUser, null);
        }
        redisUtils.expire(redisKey, duration);
    }

    public List<OnlineUser> getOnlineUsers(long pageNum, long pageSize) {
        List<OnlineUser> onlineUsers = new ArrayList<>();
        List<String> keys = redisUtils.scan(redisKeyPrefix + "*", pageNum, pageSize);
        for (String key : keys) {
            OnlineUser onlineUser = redisUtils.getObject(key, OnlineUser.class);
            onlineUsers.add(onlineUser);
        }
        return onlineUsers;
    }

    public int getOnlineUserCount() {
        return redisUtils.scanSize(redisKeyPrefix + "*");
    }
}