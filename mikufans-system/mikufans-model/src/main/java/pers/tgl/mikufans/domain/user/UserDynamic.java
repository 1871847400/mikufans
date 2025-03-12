package pers.tgl.mikufans.domain.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.util.MyUtils;

import java.util.Date;

/**
 * 用户动态
 */
@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@PermFlag(name = "用户动态", group = PermGroup.BUSINESS)
public class UserDynamic extends UserBaseEntity {
    /**
     * 转发的动态id
     */
    private Long shareId;
    /**
     * 转发理由
     */
    private String shareReason;
    /**
     * 转发次数(只增不减)
     */
    private Integer shares;
    /**
     * 业务id 动态来源
     * 转发的动态会有多条记录会引用
     */
    private Long targetId;
    /**
     * 业务类型
     * 注意业务类型和业务id必须保持一致，不然无法通过id溯源
     */
    private BusinessType dynamicType;
    /**
     * 是否置顶 0否1是
     */
    private Integer top;
    /**
     * 可见范围 0:自己 1:所有人
     */
    private Integer visible;
    /**
     * 是否定时发布 0:否 1:是
     * 如果已经过了发布时间,则会自动改为0
     */
    private Integer publishFlag;
    /**
     * 定时发布时间,默认是创建时间
     */
    private Date publishTime;
    /**
     * 是否禁用,方便其它业务控制
     */
    private Integer disabled;
    /**
     * 格式化发布时间
     */
    public String getPublishTimeStr() {
        return getPublishTime() != null ? MyUtils.formatHumanTime(getPublishTime()) : null;
    }
    /**
     * 访问动态的链接
     */
    public String getUri() {
        return "/dynamic/" + getId();
    }
}