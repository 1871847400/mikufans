package pers.tgl.mikufans.handler;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import pers.tgl.mikufans.util.MyUtils;

/**
 * 自定义redis缓存管理器,支持过期时间
 * 根据key的name来判断时间例如: key1#30s 代表key为key1,有效期30秒
 */
public class CustomRedisCacheManager extends RedisCacheManager {
    public CustomRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        int index;
        if (StrUtil.isNotBlank(name) && (index = name.lastIndexOf("#")) != -1) {
            if (index + 1 < name.length()) {
                String ttl = name.substring(index + 1);
                try {
                    cacheConfig = cacheConfig.entryTtl(MyUtils.parseDuration(ttl));
                    name = name.substring(0, index);
                } catch (Exception ignored) {}
            }
        }
        return super.createRedisCache(name, cacheConfig);
    }
}