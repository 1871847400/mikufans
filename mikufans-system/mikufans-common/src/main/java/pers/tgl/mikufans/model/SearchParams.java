package pers.tgl.mikufans.model;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import pers.tgl.mikufans.util.MathUtils;
import pers.tgl.mikufans.util.PageImpl;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
public class SearchParams implements Serializable {
    /**
     * 分页页码
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

    public <R> PageImpl<R> page() {
        return new PageImpl<R>(pageNum, pageSize, searchCount);
    }

    public <R> PageImpl<R> page(Class<R> resultType) {
        return new PageImpl<R>(pageNum, pageSize, searchCount);
    }

    public <R> PageImpl<R> page(Number total) {
        return page(Collections.emptyList(), total);
    }

    public <R> PageImpl<R> page(List<R> records, Number total) {
        PageImpl<R> page = new PageImpl<>(pageNum, pageSize, searchCount);
        page.setRecords(records);
        page.setTotal(total.intValue());
        return page;
    }

    public PageRequest pageRequest() {
        //PageRequest的size不能为0
        return PageRequest.of(pageNum - 1, MathUtils.clamp(pageSize, 1, 100));
    }
}