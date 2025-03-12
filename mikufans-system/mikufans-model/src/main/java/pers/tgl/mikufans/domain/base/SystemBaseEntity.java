package pers.tgl.mikufans.domain.base;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.domain.system.SysUser;
import pers.tgl.mikufans.jackson.sensitive.Sensitive;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
public abstract class SystemBaseEntity extends BaseEntity {
    /**
     * 创建人id (管理员账号)
     * 如果为0表示非管理员创建
     * 如果不为0表示管理员创建
     */
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @Sensitive
    private Long createBy;
    /**
     * 更新人id (管理员账号)
     * 如果使用fill=FieldFill.UPDATE代表无论什么值，都会在update语句中携带
     */
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @Sensitive
    private Long updateBy;

    @Sensitive
    public String getCreateByName() {
        return getSysUserName(createBy);
    }

    @Sensitive
    public String getUpdateByName() {
        return getSysUserName(updateBy);
    }

    @JsonIgnore
    private String getSysUserName(Long sysUserId) {
        if (sysUserId == null ||sysUserId == 0) {
            return "ROOT";
        }
        SysUser sysUser = Db.getById(sysUserId, SysUser.class);
        if (sysUser == null) {
            return "未知";
        }
        return sysUser.getUsername();
    }
}