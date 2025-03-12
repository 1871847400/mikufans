package pers.tgl.mikufans.service;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.base.BaseEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 不使用 mybatis-plus 提供的 IService 以免向外暴露多余的方法,防止破坏业务之间的联系
 * 通用方法应该尽量只定义查询,修改的方法应该由具体的service定义并暴露
 * @param <T> 数据库实体类
 */
public interface BaseService<T extends BaseEntity> {
    /**
     * 根据id获取实体
     */
    @Nullable
    T getById(Object id);
    /**
     * 关闭逻辑删除查
     */
    T findById(Object id);
    /**
     * 只查找单列属性
     */
    <R> R findById(Object id, SFunction<T, R> column);
    /**
     * 关闭逻辑删除查
     */
    <R> T findOneBy(SFunction<T, R> col, R val);

    @Nullable
    <R extends T> R getById(Object id, Class<R> resultType);
    /**
     * 用户根据id删除数据,删除操作应该保存逻辑删除并且尽量简单
     * 各个业务层应该重写此方法,达到和其它表数据的一致性和正确性
     */
    boolean removeById(@Nullable Serializable id);

    boolean removeById(@Nullable T entity);
    /**
     * 是否存在某个id(支持传实体,会自动获取其id)
     */
    boolean exists(Serializable entityOrId);
    /**
     * 是否存在某列=value的数据
     */
    boolean existsBy(SFunction<T, ?> column, @Nullable Object value);
    /**
     * 统计某列=某值的记录数
     */
    <V extends Serializable> long countBy(SFunction<T, V> col, V val);
    /**
     * 总记录数
     */
    long count();
    /**
     * 根据条件查找指定列的集合
     */
    <V> List<V> selectCols(SFunction<T, Serializable> col, Object val, SFunction<T, V> returnCol);
    /**
     * 直接列出所有对象
     */
    List<T> list();
    /**
     * 列出所有id
     */
    <R> List<R> listIds();
    /**
     * 查询某列属于指定列表的数据项列表
     */
    <R> List<T> listIn(SFunction<T, R> col, Collection<R> vals);

    <E extends IPage<T>> E page(E page);

    <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper);
    /**
     * 将实体作为条件查询列表
     */
    List<T> listByEntity(T queryEntity);
    /**
     *
     */
    <V> List<T> listBy(SFunction<T, V> col, V val);
    /**
     * 根据某列者获取对象
     */
    T getOneBy(SFunction<T, Serializable> col, Object val);
    /**
     * 根据主键获取列值
     */
    @Nullable
    <V> V getColumnValue(Serializable id, SFunction<T, V> column);
    /**
     * stream.map
     */
    default <F, A> List<A> map(List<F> list, Function<F, A> fun) {
        return list.stream().map(fun).collect(Collectors.toList());
    }

    MPJLambdaWrapper<T> wrapper();

    /**
     * 获取实体类的service对象
     * 如果能明确ModelClass,推荐使用: SpringUtil.getBean(TypeReference<BaseService<User>>() {})
     */
    @SuppressWarnings("all")
    public static <R extends BaseEntity> List<BaseService<R>> getBaseService(Class<R> entityClass) {
        List<BaseService<R>> list = new ArrayList<>(1);
        Map<String, BaseService> map = SpringUtil.getBeansOfType(BaseService.class);
        for (BaseService service : map.values()) {
            Class<?> typeArgument = ClassUtil.getTypeArgument(service.getClass(), 0);
            if (entityClass.equals(typeArgument)) {
                list.add(service);
            }
        }
        return list;
    }
}