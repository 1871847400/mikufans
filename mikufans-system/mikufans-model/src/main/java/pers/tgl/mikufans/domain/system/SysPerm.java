package pers.tgl.mikufans.domain.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;

import javax.validation.constraints.NotBlank;

/**
 * 系统权限表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@PermFlag(name = "权限管理", group = PermGroup.SYSTEM)
public class SysPerm extends SystemBaseEntity {
    /**
     * 父权限id
     */
    private Long pid;
    /**
     * 权限关键字(唯一)
     */
    @NotBlank(message = "请输入权限标识")
    private String permKey;
    /**
    * 权限名称
    */
    @NotBlank(message = "请输入权限名称")
    private String permName;
}