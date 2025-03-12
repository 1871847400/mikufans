package pers.tgl.mikufans.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.extension.mapping.base.MPJDeepService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.domain.base.BaseEntity;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.service.BaseService;
import pers.tgl.mikufans.util.DbUtils;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 注意使用baseMapper直接更新单个属性时,不会触发自动字段填充，ex:@TableField(fill = FieldFill.INSERT_UPDATE)的更新时间
 */
@SuppressWarnings("all")
@Slf4j
public abstract class BaseServiceImpl<T extends BaseEntity, M extends BaseMapper<T>> extends ServiceImpl<M, T> implements BaseService<T>, MPJDeepService<T> {
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 获取代理Service对象
     */
    protected <S extends BaseService> S getService(Class<S> cls) {
        return SpringUtil.getBean(cls);
    }
    /**
     * 广播事件
     */
    protected void publishEvent(ApplicationEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
    /**
     * 获取表信息,自带缓存
     */
    public TableInfo getTableInfo() {
        return TableInfoHelper.getTableInfo(super.currentModelClass());
    }
    /**
     * 获得当前实体的主键值
     */
    @Nullable
    protected Serializable getPKValue(T entity) {
        TableInfo tableInfo = getTableInfo();
        String keyProperty = tableInfo.getKeyProperty();
        if (keyProperty != null) {
            return (Serializable) tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
        }
        return null;
    }
    /**
     * 如果更新数量>=1则返回true
     */
//    protected boolean updateById(T entity) {
//        return SqlHelper.retBool(getBaseMapper().updateById(entity));
//    }

    /**
     * 插入一个实体,如果插入数量>=1则返回插入的实体,否则抛出异常
     */
//    @CachePut(value = "exists", key = "#entity.id")
    @Deprecated
    protected T insert(T entity) {
        if (super.save(entity)) {
            return entity;
        }
        throw new RuntimeException("创建数据实体失败");
    }

    @Override
    public T getById(Object id) {
        return super.getById((Serializable) id);
    }

    @Override
    public T findById(Object id) {
        return wrapper().eq(getTableInfo().getKeyColumn(), id)
                .disableLogicDel()
                .first();
    }

    @Override
    public <R> R findById(Object id, SFunction<T, R> column) {
        T first = wrapper().select(column)
                .eq(getTableInfo().getKeyColumn(), id)
                .disableLogicDel()
                .first();
        return first == null ? null : column.apply(first);
    }

    @Override
    public <R> T findOneBy(SFunction<T, R> col, R val) {
        return wrapper().eq(col, val)
                .disableLogicDel()
                .first();
    }

    @Override
    public <R extends T> R getById(Object id, Class<R> resultType) {
        if (!isValidId(id)) {
            return null;
        }
        return wrapper().eq(getTableInfo().getKeyColumn(), id).one(resultType);
    }

    @Override
    public List<T> list() {
        return super.list();
    }

    @Override
    public <R> List<R> listIds() {
        List<R> result = new ArrayList<>();
        List<T> list = query().select(getTableInfo().getKeyColumn()).list();
        String property = getTableInfo().getKeyProperty();
        for (T t : list) {
            result.add((R) ReflectUtil.getFieldValue(t, property));
        }
        return result;
    }

    @Override
    public List<T> list(Wrapper<T> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public <R> List<T> listIn(SFunction<T, R> col, Collection<R> vals) {
        if (CollUtil.isEmpty(vals)) {
            return Collections.emptyList();
        } else {
            return lambdaQuery().in(col, vals).list();
        }
    }

    @Override
    public <E extends IPage<T>> E page(E page) {
        return super.page(page);
    }

    @Override
    public <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper) {
        return super.page(page, queryWrapper);
    }

    /**
     * 使用任意对象完成数据库插入
     */
    @Deprecated
    public T createDto(Object dto) {
        T instance = ReflectUtil.newInstance(getEntityClass());
        BeanUtils.copyProperties(dto, instance);
        super.save(instance);
        return getById(getPKValue(instance));
    }

    /**
     * 使用任意对象完成数据库更新
     */
    @Deprecated
    public boolean updateByDto(Object dto) {
        T instance = ReflectUtil.newInstance(getEntityClass());
        BeanUtils.copyProperties(dto, instance);
        return super.updateById(instance);
    }

    public final Page<T> findPageBy(@Nullable SFunction<T, Serializable> col, @Nullable Object val, int pageNum, int pageSize) {
        Page<T> page = new PageImpl<>(pageNum, pageSize);
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
        if (col != null) {
            queryWrapper.eq(col, val);
        }
        return getBaseMapper().selectPage(page, queryWrapper);
    }

    @SafeVarargs
    public final List<T> findListBy(SFunction<T, Serializable> col, Object val, SFunction<T, ?>... searchKeys) {
        return (List<T>) find(false, col, val, searchKeys);
    }

    public List<T> findListLike(SFunction<T, Serializable> col, Object val, SFunction<T, ?>... searchKeys) {
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
        if (searchKeys != null && searchKeys.length > 0) {
            queryWrapper.select(searchKeys);
        }
        queryWrapper.like(col, val);
        return baseMapper.selectList(queryWrapper);
    }

    @Nullable
    @SafeVarargs
    public final T findOneBy(SFunction<T, Serializable> col, Object val, SFunction<T, ?>... searchKeys) {
        return (T) find(true, col, val, searchKeys);
    }

    @Override
    public T getOneBy(SFunction<T, Serializable> col, Object val) {
        return lambdaQuery().eq(col, val).one();
    }

    @Override
    public <V extends Serializable> long countBy(@Nullable SFunction<T, V> col, V val) {
        return lambdaQuery()
                .eq(col, val)
                .count();
    }

    @Override
    public long count() {
        return super.count();
    }

    @Override
    public <V> List<V> selectCols(SFunction<T, Serializable> col, Object val, SFunction<T, V> returnCol) {
        return lambdaQuery().eq(col, val)
                .select(returnCol)
                .list()
                .stream().map(returnCol)
                .collect(Collectors.toList());
    }

    @Override
    public <V> V getColumnValue(Serializable id, SFunction<T, V> column) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(column);
        queryWrapper.eq(getTableInfo().getKeyColumn(), id);
        T one = getBaseMapper().selectOne(queryWrapper);
        return one != null ? column.apply(one) : null;
    }

    /**
     * 查找满足 指定字段=指定值
     * @param searchKeys 如果不指定则查所有字段
     */
    @Nullable
    private Object find(boolean isOne, SFunction<T, Serializable> col, Object val, SFunction<T, ?>... searchKeys) {
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
        if (searchKeys != null && searchKeys.length > 0) {
            queryWrapper.select(searchKeys);
        }
        queryWrapper.eq(col, val);
        return isOne ? baseMapper.selectOne(queryWrapper) : baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据主键修改字段值
     */
    public <R> boolean updateById(Object id, SFunction<T, R> column, R value) {
//      注意实体传null 不会触发自动填充功能
        T entity = getTableInfo().newInstance();
        String keyProperty = getTableInfo().getKeyProperty();
        ReflectUtil.setFieldValue(entity, keyProperty, id);
        String implMethodName = LambdaUtils.extract(column).getImplMethodName();
        String attribeName = PropertyNamer.methodToProperty(implMethodName);
        ReflectUtil.setFieldValue(entity, attribeName, value);
        return updateById(entity);
    }

    @Override
//    @Cacheable(value = "exists", key = "#id")
    public boolean exists(Serializable entityOrId) {
        Object id = entityOrId;
        if (currentModelClass().isInstance(entityOrId)) {
            id = getPKValue((T) entityOrId);
        }
        if (id == null) {
            return false;
        }
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(getTableInfo().getKeyColumn(), id);
        return getBaseMapper().exists(queryWrapper);
    }

    @Override
    public boolean existsBy(SFunction<T, ?> column, Object value) {
        return lambdaQuery()
                .eq(value != null, column, value)
                .isNull(value == null, column)
                .exists();
    }

    @Override
    public List<T> listByEntity(T queryEntity) {
        return lambdaQuery().setEntity(queryEntity).list();
    }

    @Override
    public <V> List<T> listBy(SFunction<T, V> col, V val) {
        return lambdaQuery().eq(col, val).list();
    }

    @Override
    public boolean removeById(@Nullable Serializable id) {
        return isValidId(id) && this.removeById(getById(id));
    }

    /**
     * 子类可以重写以添加额外的逻辑
     */
    @Override
    public boolean removeById(@Nullable T entity) {
        if (entity != null) {
            if (entity instanceof UserBaseEntity) {
                checkUserPermission(((UserBaseEntity) entity));
            } else if (entity instanceof SystemBaseEntity) {
                UserToken userToken = SecurityUtils.getContextUserToken();
                Long createBy = ((SystemBaseEntity) entity).getCreateBy();
                if (userToken == null || userToken.getUserType() != 1 || userToken.getId() != createBy) {
                    throw new CustomException(Code.FORBIDDEN);
                }
            }
            return super.removeById(entity);
        }
        return false;
    }

    protected void checkUserPermission(UserBaseEntity entity) {
        if (entity == null) {
            throw new CustomException(Code.RESOURCE_NOT_FOUND);
        }
        UserToken userToken = SecurityUtils.getContextUserToken();
        if (userToken == null || !Objects.equals(userToken.getId(), entity.getUserId()) && userToken.getUserType() != 1) {
            throw new CustomException(Code.FORBIDDEN);
        }
    }

    @Override
    public MPJLambdaWrapper<T> wrapper() {
        return new MPJLambdaWrapper(getEntityClass());
    }

    /**
     * 求某列的最大值
     */
    @Nullable
    public <R> R maxBy(SFunction<T, R> column) {
        T one = wrapper().selectMax(column).one();
        return one != null ? column.apply(one) : null;
    }

    protected boolean isValidId(Object id) {
        return id != null && NumberUtil.parseLong(id.toString(), 0L) > 0;
    }

    protected <R extends Number> Optional<R> getMax(SFunction<T, R> column, SFunction<T, ?> condition, Object value) {
        T one = wrapper().selectMax(VideoPart::getSort)
                .eq(condition, value)
                .one();
        return Optional.ofNullable(one != null ? column.apply(one) : null);
    }

    /**
     * 增加/减少某列的值,如果超出数字范围则设置为0
     */
    protected <V extends Number> boolean incrementById(Object id, SFunction<T, V> column, V value) {
        String colName = DbUtils.inferColumnName(column);
        synchronized (("incr:" + id + ":" + colName).intern()) {
            try {
                return DbUtils.increment(column, value, BaseEntity::getId, id);
            } catch (Exception e) {
                if (e.getCause() instanceof MysqlDataTruncation) {
                    return lambdaUpdate().eq(BaseEntity::getId, id)
                            .set(column, 0)
                            .update(ReflectUtil.newInstance(getEntityClass()));
                } else {
                    e.printStackTrace();
                    return false;
                }
            }
        }
    }
}