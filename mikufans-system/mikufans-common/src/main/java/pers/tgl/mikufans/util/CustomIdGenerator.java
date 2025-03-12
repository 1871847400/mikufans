package pers.tgl.mikufans.util;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;

/**
 * 如果注入容器会覆盖默认的雪花算法生成器, IdType 必须为 ASSIGN_ID 或 ASSIGN_UUID
 */
//@Component
public class CustomIdGenerator implements IdentifierGenerator {
    @Override
    public Number nextId(Object entity) {
        return System.currentTimeMillis()/1000;
    }
}