package pers.tgl.mikufans.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Sort;
import pers.tgl.mikufans.model.SearchParams;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoStarParams extends SearchParams {
    /**
     * 指定收藏夹id
     */
    @NotNull(message = "请指定收藏夹")
    private Long starId;
    /**
     * 视频名称
     */
    @Length(max = 32)
    private String title;
    /**
     * 排序规则
     * 默认按收藏时间降序
     */
    private Sort.Direction dir = Sort.Direction.DESC;
}