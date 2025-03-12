package pers.tgl.mikufans.domain.system;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UniqueElements;
import pers.tgl.mikufans.anno.Encrypt;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.domain.provider.RoleProvider;
import pers.tgl.mikufans.form.*;
import pers.tgl.mikufans.validator.group.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 后台管理员账户
 */
@EqualsAndHashCode(callSuper = true)
@Data
@PermFlag(name = "管理员账号", group = PermGroup.SYSTEM)
@TableName(autoResultMap = true)
@Form(name = "管理员账号", labelPosition = "right", labelWidth = "100px")
public class SysUser extends SystemBaseEntity {
    /**
     * 用户名
     */
    @NotBlank(message = "请填写用户名", groups = Create.class)
    @Length(max = 32, message = "名称太长了")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @FormItem(type = FormItemType.TEXT, label = "用户名", placeholder = "请输入用户名", required = Scope.BOTH, maxlength = 32)
    private String username;
    /**
     * 密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "请填写密码", groups = Create.class)
    @Length(min = 6, max = 32, message = "密码长度6-32位")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @Encrypt
    @FormItem(type = FormItemType.PASSWORD, label = "密码", placeholder = "请输入密码", required = Scope.CREATE, maxlength = 32)
    private String password;
    /**
     * 头像id
     */
    @FormItem(type = FormItemType.IMAGE, label = "头像", placeholder = "请上传头像")
    @FormImage(width = "60px", height = "60px", radius = "50%")
    private Long avatarId;
    /**
     * 角色id列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    @UniqueElements(message = "角色重复")
    @FormItem(type = FormItemType.SELECT, label = "角色列表", placeholder = "请选择角色")
    @FormSelect(provider = RoleProvider.class, multiple = true)
    private List<@NotNull Long> roleIds;
    /**
     * 是否禁用 0正常1禁用
     */
    @Range(min = 0, max = 1)
    @FormItem(type = FormItemType.RADIO, label = "是否禁用", placeholder = "请选择是否禁用", required = Scope.BOTH, dict = "is_status", value = "0")
    private Integer disabled;
}