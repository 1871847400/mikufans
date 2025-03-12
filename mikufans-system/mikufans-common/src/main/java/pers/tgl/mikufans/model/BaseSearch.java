package pers.tgl.mikufans.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.TypeUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.util.PageImpl;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 通用搜索实体
 */
@Data
@Deprecated
public class BaseSearch<T> implements Serializable {
    /**
     * 作为查询条件的Map
     * 值为null的会被转为 is null
     */
    @Null
    @Deprecated
    private Map<SFunction<T, ?>, Object> allEqMap;
    /**
     * 查询一组id
     */
    @Deprecated
    private List<Long> ids;
    /**
     * 分页
     */
    @NotNull
    @Min(value = 1)
    private int pageNum = 1;
    /**
     * 每页条数 (禁止同时使用pageSize和selectCollection)
     * 有时会让pageSize为0，目的是查total
     */
    @NotNull
    @Min(value = 0)
    private int pageSize = 10;
    /**
     * 是否先查找总数,否则page的total属性为null
     */
    @NotNull
    private boolean searchCount = true;
    /**
     * 默认降序查找
     */
    @NotNull
    private boolean asc = false;
    /**
     * 搜索关键字
     */
    @Length(max = 64, message = "搜索关键字不能超过64个字符")
    private String keyword;

    public <R> PageImpl<R> createPage() {
        return new PageImpl<>(pageNum, pageSize, searchCount);
    }

    public <R> void addEq(SFunction<T, R> col, R value) {
        if (this.allEqMap == null) {
            this.allEqMap = new LinkedHashMap<>();
        }
        this.allEqMap.put(col, value);
    }

    /**
     * 获取泛型T的class,必须继承该类后使用
     */
    @SuppressWarnings("all")
    private Class<T> getEntityClass() {
        return (Class<T>) TypeUtil.getTypeArgument(getClass());
    }

    @Deprecated
    public <R> PageImpl<R> page(MPJLambdaWrapper<T> wrapper, @Nullable Class<R> resultType) {
        if (resultType == null) {
            resultType = (Class<R>) wrapper.getEntityClass();
        }
        PageImpl<R> page = createPage();
        wrapper.selectAll(wrapper.getEntityClass())
                .distinct() //将所有select字段组合起来去重
                .allEq(Objects.nonNull(this.getAllEqMap()), this.getAllEqMap(), true)
                .page(page, resultType);
        return page;
    }

    @Deprecated
    public MPJLambdaWrapper<T> wrapper() {
        return wrapper(getEntityClass(), "t");
    }

    @Deprecated
    public <R> MPJLambdaWrapper<R> wrapper(Class<R> modelClass, String alias) {
        return new MPJLambdaWrapper<>(modelClass, alias)
                .selectAll(modelClass)
                .distinct()
                .in(CollUtil.isNotEmpty(ids),"id", ids)
                .allEq(Objects.nonNull(this.getAllEqMap()), this.getAllEqMap(), true);
    }

    @Deprecated
    public <R, S extends BaseSearch<T>> S one(SFunction<T, R> column, R value) {
        this.setPageNum(1);
        this.setPageSize(1);
        this.setSearchCount(false);
        this.addEq(column, value);
        return (S) this;
    }
}