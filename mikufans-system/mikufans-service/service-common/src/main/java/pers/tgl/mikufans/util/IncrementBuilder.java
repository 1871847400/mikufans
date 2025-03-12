package pers.tgl.mikufans.util;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.AllArgsConstructor;

import java.util.*;

/**
 * 方便对数据库做增加的工具
 */
@Deprecated
public class IncrementBuilder<T> {
    private final Class<T> entityClass;
    private final List<Increment<T>> increments;
    private final Map<String, Object> conditions;

    public IncrementBuilder(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.increments = new ArrayList<>();
        this.conditions = new LinkedHashMap<>();
    }

    public IncrementBuilder<T> id(Object id) {
        String keyColumn = TableInfoHelper.getTableInfo(entityClass).getKeyColumn();
        conditions.put(keyColumn, id);
        return this;
    }

    public <V> IncrementBuilder<T> eq(SFunction<T, V> col, V val) {
        String columnName = DbUtils.inferColumnName(col);
        conditions.put(columnName, val);
        return this;
    }

    public IncrementBuilder<T> add(SFunction<T, Number> col, Number delta) {
        increments.add(new Increment<T>(col, delta));
        return this;
    }

    public boolean execute() {
        if (increments.isEmpty() || conditions.isEmpty()) {
            return false;
        }
        UpdateChainWrapper<T> wrapper = Db.update(entityClass);
        for (Map.Entry<String, Object> e : conditions.entrySet()) {
            wrapper.eq(e.getKey(), e.getValue());
        }
        for (Increment<T> inc : increments) {
            wrapper.set(DbUtils.inferColumnName(inc.col), inc.col + " + " + inc.delta);
        }
        //触发自动填充
        T instance = ReflectUtil.newInstance(entityClass);
        return wrapper.update(instance);
    }

    @AllArgsConstructor
    public static class Increment<T> {
        private final SFunction<T, Number> col;
        private final Number delta;
    }
}