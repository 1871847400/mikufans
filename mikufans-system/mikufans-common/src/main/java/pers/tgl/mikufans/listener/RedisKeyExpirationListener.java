package pers.tgl.mikufans.listener;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监听redis中的key到期，注意tomcat关闭状态下无法被通知
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    /**
     * RedisKey : 处理函数
     */
    private final Map<String, Runnable> handlers = new ConcurrentHashMap<>();

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    public void handleOnce(String redisKey, Runnable handler) {
        handlers.put(redisKey, handler);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String redisKey = new String(message.getBody());
        if (StrUtil.isNotBlank(redisKey)) {
            Runnable handler = handlers.remove(redisKey);
            if (handler != null) {
                handler.run();
            }
        }
    }
}