package pers.tgl.mikufans.domain.user;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.validator.group.Update;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@PermFlag(name = "用户举报", group = PermGroup.BUSINESS)
public class UserReport extends UserBaseEntity {
    /**
     * 目标业务id
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long targetId;
    /**
     * 举报的业务类型
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private BusinessType reportType;
    /**
     * 举报行为
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long behaviorId;
    /**
     * 详细理由
     * 不能修改，如果要修改先删除旧的,再创建新的举报
     */
    @TableField(whereStrategy = FieldStrategy.NEVER)
    private String reason;
    /**
     * 审核状态：0未审核 1审核通过 2审核未通过
     * 审核过后无法再次审核
     */
    @NotNull(message = "请选择审核状态", groups = Update.class)
    private AuditStatus audit;
    /**
     * 举报的行为名称
     */
    @TableField(exist = false)
    private String behaviorName;
}