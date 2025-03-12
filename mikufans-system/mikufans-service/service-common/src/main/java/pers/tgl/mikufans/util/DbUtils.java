package pers.tgl.mikufans.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.config.upload.LimitConfig;
import pers.tgl.mikufans.domain.base.BaseEntity;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.exception.CustomException;

import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("all")
public class DbUtils {
    /**
     * 根据lambda函数推断数据库字段名
     * getCreateTime() => create_time
     */
    public static String inferColumnName(@Nullable SFunction<?, ?> func) {
        if (func == null) {
            return null;
        }
        //例如: getCreateTime
        String implMethodName = LambdaUtils.extract(func).getImplMethodName();
        //例如：createTime
        String attribeName = PropertyNamer.methodToProperty(implMethodName);
        //例如：create_time
        return CharSequenceUtil.toUnderlineCase(attribeName);
    }

    @Nullable
    public static Long getUserIdBy(Class<? extends UserBaseEntity> entityClass, Long id) {
        UserBaseEntity baseEntity = new MPJLambdaWrapper<>(entityClass)
                .disableLogicDel()
                .select(CharSequenceUtil.toUnderlineCase(UserBaseEntity.Fields.userId))
                .eq(BaseEntity.Fields.id, id)
                .one();
        return baseEntity != null ? baseEntity.getUserId() : null;
    }

    @Nullable
    public static <T extends BaseEntity, R> R getColumn(SFunction<T, R> column, Object id, R defVal) {
        Class<T> entityClass = (Class<T>) LambdaUtils.extract(column).getInstantiatedClass();
        T entity = Db.lambdaQuery(entityClass)
                .eq(BaseEntity::getId, id)
                .select(column)
                .one();
        return entity != null ? column.apply(entity) : defVal;
    }

    /**
     * 让某个数字型字段加上一个数
     * xxx = xxx + -1 支持+负数
     * 注意unsigned字段,不能出现负数
     * 可能会出现异常 MysqlDataTruncation 数字溢出和不能为负数的异常
     * @param column 需要修改的属性
     * @param value 增加的数量(可负数)
     * @return 至少一条数据被修改
     */
    public static <T extends BaseEntity, R extends Number, S> boolean increment(SFunction<T, R> column, R value, SFunction<T, S> condition, S ...vals) {
        if (vals == null || vals.length == 0) {
            return false;
        }
        Class<Object> entityClass = (Class<Object>) LambdaUtils.extract(column).getInstantiatedClass();
        String inCol = inferColumnName(condition);
        String col = inferColumnName(column);
        String sql = String.format("%s = %s + %s", col, col, value);
        Object instance = ReflectUtil.newInstance(entityClass);
        return Db.update(entityClass)
                .setSql(sql)
                .in(vals.length > 1, inCol, vals)
                .eq(vals.length == 1, inCol, vals[0])
                .update(instance); //触发自动填充
    }

    public static void checkLimit(Class<? extends UserBaseEntity> table, LimitConfig limit) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        if (SecurityUtils.isAdminUser()) {
            return;
        }
        String today = DateUtil.today();
        String userCol = "t.user_id";
        if (limit.getDaily() != -1) {
            Long count = new MPJLambdaWrapper<>(table, "t")
                    .eq("t.user_id", contextUserId)
                    .apply("DATE(t.create_time) = {0}", today)
                    .count();
            if (count >= limit.getDaily()) {
                throw new CustomException("超出今日最大上传数量");
            }
        }
        int week = DateUtil.thisWeekOfYear();
        if (limit.getWeek() != -1) {
            Long count = new MPJLambdaWrapper<>(table, "t")
                    .eq("t.user_id", contextUserId)
                    .apply("WEEK(t.create_time) = {0}", week)
                    .count();
            if (count >= limit.getWeek()) {
                throw new CustomException("超出本周最大上传数量");
            }
        }
        if (limit.getTotal() != -1) {
            Long count = Db.lambdaQuery(table)
                    .eq(UserBaseEntity::getUserId, contextUserId)
                    .count();
            if (count >= limit.getTotal()) {
                throw new CustomException("超出最大上传数量");
            }
        }
    }

    /**
     * 根据java中属性名来获取对应表中的列名
     */
    @NonNull
    public static String getColumnName(Class<?> entityType, String property) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityType);
        if (tableInfo != null) {
            for (TableFieldInfo tableFieldInfo : tableInfo.getFieldList()) {
                if (tableFieldInfo.getProperty().equals(property)) {
                    return tableFieldInfo.getColumn();
                }
            }
        }
        return property;
    }

    /**
     * 获取数据库表所有字段
     */
    public static Set<String> getColumns(Class<?> entityType) {
        Set<String> result = new LinkedHashSet<>();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityType);
        if (tableInfo != null) {
            if (StrUtil.isNotBlank(tableInfo.getKeyColumn())) {
                result.add(tableInfo.getKeyColumn());
            }
            for (TableFieldInfo tableFieldInfo : tableInfo.getFieldList()) {
                result.add(tableFieldInfo.getColumn());
            }
        }
        return result;
    }
}