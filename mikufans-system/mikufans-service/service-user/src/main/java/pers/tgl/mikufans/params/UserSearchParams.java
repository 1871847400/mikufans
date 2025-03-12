package pers.tgl.mikufans.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import pers.tgl.mikufans.consts.UserSearchSort;
import pers.tgl.mikufans.model.SearchParams;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserSearchParams extends SearchParams {
    /**
     * 模糊查找昵称
     */
    @Length(max = 32)
    private String nickname;
    /**
     * 是否返回最新动态
     */
    private boolean lastDynamic = false;
    /**
     * 排序规则
     */
    @NotNull
    private UserSearchSort sort = UserSearchSort.DEFAULT;
}