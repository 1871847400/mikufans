package pers.tgl.mikufans.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 返回格式: { records:[...],total:0,size:10,current:1,pages:0 }
 * total和pages 需要 searchCount=true，否则为0
 * 1.去除一些不必要的数据再传给前端
 * 2.由于前端精度不够，全局将long序列化为string，但page的部分属性需要为数字类型，序列化器toString改为toLong
 *
 * 注意：如果在连表查询中使用orderItems,相同字段会使用主表的(因为子表字段会被设置别名)
 */
@EqualsAndHashCode(callSuper = true)
@Data
//@JsonIgnoreProperties({"orders","optimizeCountSql","optimizeJoinOfCountSql"})
public class PageImpl<T> extends Page<T> {
    public PageImpl() {
        //设置最大分页大小,SQL中的limit将不会大于maxLimit
        setMaxLimit(100L);
    }

    public PageImpl(int current, int size) {
        super(current, size);
    }

    public PageImpl(int current, int size, int total) {
        super(current, size, total);
    }

    public PageImpl(int current, int size, boolean searchCount) {
        super(current, size, searchCount);
    }

    public PageImpl(int current, int size, int total, boolean searchCount) {
        super(current, size, total, searchCount);
    }

    public static <T> PageImpl<T> of(int pageNum, int pageSize, int total, List<T> records) {
        PageImpl<T> page = new PageImpl<>(pageNum, pageSize, total, false);
        page.setRecords(records);
        return page;
    }

    @Override
    @JsonSerialize(using = LongSerializer.class)
    public long getCurrent() {
        return super.getCurrent();
    }

    @Override
    @JsonSerialize(using = LongSerializer.class)
    public long getTotal() {
        return super.getTotal();
    }

    @Override
    @JsonSerialize(using = LongSerializer.class)
    public long getSize() {
        return super.getSize();
    }

    @Override
    @JsonSerialize(using = LongSerializer.class)
    public long getPages() {
        return super.getPages();
    }

    public PageImpl<T> fill(Consumer<T> consumer) {
        getRecords().forEach(consumer);
        return this;
    }

    @Override
    public Page<T> setRecords(List<T> records) {
        return super.setRecords(records.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public boolean isEmpty() {
        return getRecords().isEmpty();
    }

    //使用@ToString无效
    @Override
    public String toString() {
        return "PageImpl{" +
                "records=" + records +
                ", total=" + total +
                ", size=" + size +
                ", current=" + current +
                ", orders=" + orders +
                ", optimizeCountSql=" + optimizeCountSql +
                ", searchCount=" + searchCount +
                ", optimizeJoinOfCountSql=" + optimizeJoinOfCountSql +
                ", maxLimit=" + maxLimit +
                ", countId='" + countId + '\'' +
                '}';
    }

    public static <T> PageImpl<T> empty() {
        return new PageImpl<>(0, 0, 0, false);
    }
}