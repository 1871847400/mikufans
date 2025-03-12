package pers.tgl.mikufans.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import pers.tgl.mikufans.consts.FollowUserSort;
import pers.tgl.mikufans.model.SearchParams;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserFollowParams extends SearchParams {
    /**
     *
     */
    @Length(max = 64)
    private String keyword;
    /**
     * 指定用户id
     */
    private Long userId;
    /**
     * 排序规则
     */
    @NotNull
    private FollowUserSort sort = FollowUserSort.FOLLOW_TIME;

    private Boolean asc;
}