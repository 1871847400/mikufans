package pers.tgl.mikufans.domain.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.UniqueElements;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 系统角色表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(autoResultMap = true)
@PermFlag(name = "角色管理", group = PermGroup.SYSTEM)
public class SysRole extends SystemBaseEntity {
    /**
     * 角色标识(唯一)
     */
    @NotBlank(message = "请输入角色标识")
    private String roleKey;
    /**
     * 角色名称
     */
    @NotBlank(message = "请输入角色名称")
    private String roleName;
    /**
     * 权限id列表   包含父权限id和子权限id
     * 父权限用于判断整个页面的权限
     * 子权限用于判断是否有某个按钮等更细节的权限
     * 如果没有父权限,其对应的子权限应该不可见
     */
    @UniqueElements(message = "权限重复")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<@NotNull Long> permIds;
}