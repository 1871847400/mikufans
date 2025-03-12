package pers.tgl.mikufans.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 修改排序的通用实体模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto implements Serializable {
    /**
     * 需要传递所有id,排序按照列表的顺序
     * 传递所有id防止因为网络延迟造成前端看到的和后端顺序不一致
     */
    @NotNull
    @UniqueElements
    private List<@NotNull Long> ids;
}