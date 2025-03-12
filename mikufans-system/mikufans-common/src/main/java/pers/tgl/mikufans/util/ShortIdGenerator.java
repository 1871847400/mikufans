package pers.tgl.mikufans.util;

import java.util.*;

public class ShortIdGenerator {
    public static final ShortIdGenerator Instance = new ShortIdGenerator();

    private ShortIdGenerator() {}
    /**
     * 时间戳基准值: 2022-01-01 00:00:00 (一旦确定,不可变)
     * 由于时间戳会突破位数(最多62年),所以把本来的基准值1970年变成2022年
     */
    private static final int twepoch = 1640966400;
    /**
     * 记录上一次使用的id,避免并发产生相同id
     */
    private final Map<String, Long> lastIdMap = new HashMap<>();
    /**
     * 获取当前秒的时间戳,例如: 1716199699
     * 将精确到秒的时间戳作为id,如果id已经被使用则+1
     */
    public Long nextId(Class<?> cls) {
        final String className = cls.getName();
        synchronized (className) {
            long timestamp = System.currentTimeMillis() / 1000;
            long id = timestamp - twepoch;
            Long lastId = lastIdMap.get(className);
            if (lastId != null && lastId >= id) {
                id = lastId + 1;
            }
            lastIdMap.put(className, id);
            return id;
        }
    }
}