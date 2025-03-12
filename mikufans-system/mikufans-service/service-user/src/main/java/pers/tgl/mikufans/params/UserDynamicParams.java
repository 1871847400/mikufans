package pers.tgl.mikufans.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.model.SearchParams;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDynamicParams extends SearchParams {
    /**
     * 关键字
     */
    @Length(max = 64, message = "搜索关键字太长了")
    private String keyword;
    /**
     * 搜索指定用户的动态
     * 不指定则为：自己+关注用户的动态
     */
    private Long userId;
    /**
     * 限制动态类型
     */
    private BusinessType dynamicType;
    /**
     * 限制转发id,转发了某条动态的所有动态
     * 0代表非转发的动态
     */
    private Long shareId;
    /**
     * 排除自己
     */
    private boolean excludeSelf = false;
}