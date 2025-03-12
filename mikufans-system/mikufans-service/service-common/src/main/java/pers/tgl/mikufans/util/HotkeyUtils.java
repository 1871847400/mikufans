package pers.tgl.mikufans.util;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 使用redis记录热搜
 */
//@Component
@Deprecated
public class HotkeyUtils {
    public static final String REDIS_SEARCH_HOTKEY = "search:hotkey";
    public static final long REDIS_SEARCH_HOTKEY_LENGTH = 10000;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 增加指定关键词的搜索次数(分数)
     * 应该限制最高记录N个关键词,超过则删除排名低的
     */
    @Async
    public void increment(String keyword, int amount) {
        redisUtils.zincrby(REDIS_SEARCH_HOTKEY, amount, keyword);
        Long size = redisUtils.zcard(REDIS_SEARCH_HOTKEY);
        if (size != null && size > REDIS_SEARCH_HOTKEY_LENGTH) {
            redisUtils.zremRangeByRank(REDIS_SEARCH_HOTKEY, REDIS_SEARCH_HOTKEY_LENGTH, -1);
        }
    }

    /**
     * 获得前N名的热词
     */
    public List<String> getHotkeys(@Nullable String keyword, int rank) {
        rank = MathUtils.clamp(rank, 1, 100);
        if (StrUtil.isBlank(keyword)) {
            return redisUtils.zrange(REDIS_SEARCH_HOTKEY, 0, rank - 1)
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        } else {
            return ListUtil.reverse(redisUtils.zscan(REDIS_SEARCH_HOTKEY, "*" + keyword + "*", rank)
                    .stream()
                    .map(ZSetOperations.TypedTuple<Object>::getValue)
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toList())
            );
        }
    }
}