package pers.tgl.mikufans.util;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis操作类
 *  方法名尽量按照命令名写
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUtils {
    private final RedisTemplate<String, Object> redisTemplate;

    public <T> T execute(RedisCallback<T> redisCallback) {
        return redisTemplate.execute(redisCallback);
    }

    /**
     * 是否存在某个key
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 模糊查询key列表 *匹配任意多个字符 ?匹配单个字符 []匹配括号内的某个字符
     * keys * 查询所有
     * keys *hel*  => hello hellog ahellog
     * keys h[ae]llo => hello hallo
     * keys h[^e]llo => 除了hello
     *
     */
    public Set<String> keys(String str) {
        try {
            return redisTemplate.keys(str);
        } catch (Exception e) {
            return new HashSet<>(0);
        }
    }

    /**
     * 模糊查询key列表
     * 指令格式: scan 0 match *ll* count 2
     * 格式解释：scan 游标(每次结果的第一个数字就是下个游标的起点) match 匹配规则 count 每次查找数量
     */
    public List<String> scan(String pattern) {
        List<String> keys = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(pattern)
                .count(1000)
                .build();
        try (Cursor<String> cursor = redisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }
        }
        return keys;
    }
    public List<String> scan(String pattern, long pageNum, long pageSize) {
        List<String> keys = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(pattern)
                .count(pageSize)
                .build();
        try (Cursor<String> cursor = redisTemplate.scan(scanOptions)) {
            //先偏移一部分
            for (long l = pageSize; l < pageNum * pageSize; l++) {
                if (cursor.hasNext()) {
                    cursor.next();
                } else {
                    break;
                }
            }
            for (long l = 0; l < pageSize; l++) {
                if (cursor.hasNext()) {
                    keys.add(cursor.next());
                } else {
                    break;
                }
            }
        }
        return keys;
    }
    public int scanSize(String pattern) {
        int size = 0;
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(pattern)
                .count(1000)
                .build();
        try (Cursor<String> cursor = redisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                cursor.next();
                size++;
            }
        }
        return size;
    }


    /**
     * 当 key 不存在时，返回 -2 。 当 key 存在但没有设置剩余生存时间时，返回 -1 。
     * 否则，以毫秒为单位，返回 key 的剩余生存时间
     */
    public Long ttl(String key, TimeUnit timeUnit) {
        try {
            return redisTemplate.getExpire(key, timeUnit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 以字符串形式获取对象
     */
    public String getString(String key, @Nullable String defaultValue) {
        Object obj = null;
        try {
            obj = redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("getString error", e);
            return defaultValue;
        }
        if (obj == null) {
            return defaultValue;
        }
        if (obj instanceof String || ClassUtil.isBasicType(obj.getClass())) {
            return obj.toString();
        }
        return JsonUtils.writeString(obj);
    }

    @Nullable
    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * @param key
     * @param type 不能使用原始类型
     */
    @Nullable
    public <T> T getObject(String key, Class<T> type) {
        String str = getString(key);
        return StrUtil.isBlank(str) ? null : JsonUtils.read(str, type);
    }

    /**
     * 返回字符串长度
     */
    @Nullable
    public Long strlen(String key) {
        try {
            return redisTemplate.opsForValue().size(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置string类型
     * @param duration 设置为null永不过期
     */
    public boolean set(String key, Object value, @Nullable Duration duration) {
        try {
            if (duration != null) {
                redisTemplate.opsForValue().set(key, value, duration);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 如果key不存在且保存时间大于0则创建成功
     *  setNX = set if not exists
     */
    public boolean setNX(String key, Object value, long timeMS) {
        if (timeMS > 0) {
            try {
                return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, timeMS, TimeUnit.MILLISECONDS));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean setNX(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 增加数,如果key不存在则自动创建
     * @return 返回加上后的值
     */
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long decrement(String key, long delta) {
        try {
            return redisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String hget(String key, String hashKey) {
        Object obj = redisTemplate.opsForHash().get(key, hashKey);
        if (obj == null) {
            return null;
        }
        if (ClassUtil.isBasicType(obj.getClass()) || obj instanceof String) {
            return obj.toString();
        }
        return JsonUtils.writeString(obj);
    }

    /**
     * hash表获取值
     * @param type 不能是基本类型
     */
    @Nullable
    public <T> T hget(String key, String hashKey, Class<T> type) {
        Object obj = redisTemplate.opsForHash().get(key, hashKey);
        if (obj == null) {
            return null;
        }
        return JsonUtils.read(obj.toString(), type);
    }

    public boolean hset(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 如果hashkey不存在才设置成功
     */
    public boolean hsetnx(String key, String hashKey, Object value) {
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 返回哈希表所有字段的值
     */
    public List<Object> hvals(String key) {
        try {
            return redisTemplate.opsForHash().values(key);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * 直接获取hash表
     * @param key
     * @return
     */
    public Map<Object, Object> hgetall(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    /**
     * 判断指定hashkey是否存在
     */
    public Boolean hexists(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 为 hash类型 增加值，返回增加后的值
     * @param hashKey 只支持integer
     * @param delta 可以是负数
     */
    public Long increment(String key, Object hashKey, long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * 让一个key在指定时间后过期
     * @param key 支持 string, hash...
     */
    public boolean expire(String key, Duration duration) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, duration));
    }

    public boolean expireAt(String key, Date date) {
        return Boolean.TRUE.equals(redisTemplate.expireAt(key, date));
    }

    public Long del(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                if (Boolean.TRUE.equals(redisTemplate.delete(keys[0]))) {
                    return 1L;
                }
            } else {
                return redisTemplate.delete(Arrays.asList(keys));
            }
        }
        return 0L;
    }

    public Long hdel(String key, Object... hashKeys) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKeys);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * list类型 从列表左边插入值
     */
    public Long lpush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 从列表右边插入值
     */
    public Long rpush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * list类型 从两边弹出值
     */
    public Object lpop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回列表中指定区间内的元素，区间以偏移量 START 和 END 指定。
     * 其中 0 表示列表的第一个元素， 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     */
    public List<Object> lrange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Object rpop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * list类型 返回大小
     */
    public long llen(String key) {
        Long size = redisTemplate.opsForList().size(key);
        return size == null ? 0 : size;
    }

    /**
     * 往无序集合 set 中添加数据,如果已存在则不会添加
     * @return 返回成功添加的数量,如果为null则添加失败
     */
    public Long sadd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取 set 的大小
     * @return null获取失败
     */
    public Long scard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 判断member是否包含在set中
     */
    public Boolean sismember(String key, Object member) {
        return redisTemplate.opsForSet().isMember(key, member);
    }

    /**
     * 返回集合中的所有的成员,不会移除元素
     */
    public Set<Object> smemebers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public Long srem(String key, Object ...values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 其中成员的位置按分数值递增(从小到大)来排序。
     * 具有相同分数值的成员按字典序(lexicographical order )来排列。
     * @param end -1代表到最后一个,-2倒数第二个
     */
    public Set<Object> zrange(String key, long start, long end) {
        Set<Object> set;
        try {
            set = redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            set = new HashSet<>(0);
        }
        return set;
    }

    /**
     * 返回分数在 min<=score<=max 区间的成员
     * 按分数从小到大顺序
     */
    public Set<Object> zrangeByScore(String key, double min, double max) {
        Set<Object> set;
        try {
            set = redisTemplate.opsForZSet().rangeByScore(key, min, max);
        } catch (Exception e) {
            set = new HashSet<>(0);
        }
        return set;
    }

    /**
     * 从大到小
     */
    public Set<Object> zrevRangeByScore(String key, double min, double max) {
        Set<Object> set;
        try {
            set = redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
        } catch (Exception e) {
            set = new HashSet<>(0);
        }
        return set;
    }

    /**
     * 删除在指定排名区间的成员
     */
    public Long zremRangeByRank(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().removeRange(key, start, end);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * zset给指定成员增加分数
     */
    public void zincrby(String key, double increment, Object member) {
        redisTemplate.opsForZSet().incrementScore(key, member, increment);
    }

    /**
     * 计算zset的大小
     */
    public Long zcard(String key) {
        try {
            return redisTemplate.opsForZSet().zCard(key);
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean zadd(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 按照条件寻找符合的key列表，默认按照score升序
     */
    public List<ZSetOperations.TypedTuple<Object>> zscan(String key, String pattern, @Nullable Integer count) {
        List<ZSetOperations.TypedTuple<Object>> result = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(pattern) //key匹配规则: abc*
                .count(100) //每次迭代返回的条数,值越大和redis服务来回次数越少
                .build();
        try (Cursor<ZSetOperations.TypedTuple<Object>> cursor = redisTemplate.opsForZSet().scan(key, scanOptions)) {

            while (cursor.hasNext()) {
                ZSetOperations.TypedTuple<Object> next = cursor.next();
                result.add(next);
                if (count != null && result.size() >= count) {
                    break;
                }
            }
        }
        return result;
    }
}