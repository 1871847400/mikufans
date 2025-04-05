package pers.tgl.mikufans.domain.user;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.lang.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pers.tgl.mikufans.anno.Encrypt;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.form.*;
import pers.tgl.mikufans.jackson.IntegerSerializer;
import pers.tgl.mikufans.jackson.sensitive.Sensitive;
import pers.tgl.mikufans.jackson.sensitive.SensitiveType;
import pers.tgl.mikufans.validator.group.Create;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户表
 * @TableName user
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="user", autoResultMap = true)
@FieldNameConstants
@Data
@PermFlag(name = "用户管理", group = PermGroup.BUSINESS)
@Form(name = "用户", labelPosition = "right", labelWidth = "80px")
public class User extends SystemBaseEntity implements UserDetails {
    /**
     * 用户名(一般为邮箱)
     */
    @Sensitive(SensitiveType.EMAIL)
    @TableField(condition = SqlCondition.LIKE_RIGHT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @NotBlank(message = "请输入邮箱", groups = Create.class)
    @Length(max = 100, message = "邮箱太长了")
    @FormItem(type = FormItemType.TEXT, label = "邮箱", placeholder = "请输入邮箱", required = Scope.BOTH, maxlength = 100)
    private String username;
    /**
     * 昵称
     */
    @TableField(condition = SqlCondition.LIKE_RIGHT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @NotBlank(message = "请输入昵称", groups = Create.class)
    @Length(min = 2, max = 16, message = "昵称长度2-16位")
    @Pattern(regexp = "\\S+", message = "昵称不能包含空格") //不匹配才会触发
    @FormItem(type = FormItemType.TEXT, label = "昵称", placeholder = "请输入昵称", required = Scope.BOTH, maxlength = 16)
    private String nickname;
    /**
     * 密码,从数据库拿到的并非原始密码
     * JsonProperty设置了只允许反序列化而不允许序列化
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Encrypt
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @NotBlank(message = "请输入密码", groups = Create.class)
    @Length(min = 6, max = 24, message = "密码长度6-24位")
    @FormItem(type = FormItemType.PASSWORD, label = "密码", placeholder = "请输入密码", required = Scope.BOTH, maxlength = 24)
    private String password;
    /**
     * 性别,0保密1男2女
     */
    @FormItem(type = FormItemType.SELECT, label = "性别", placeholder = "请选择性别", dict = "user_gender", value = "0")
    @Range(min = 0, max = 2)
    private Integer gender;
    /**
     * 出生日期
     */
    @Nullable
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "出生日期不合法")
    @FormItem(type = FormItemType.DATE, label = "出生日期", placeholder = "请选择出生日期", timeFormat = "YYYY-MM-DD")
    private Date birthday;
    /**
     * 个性签名
     */
    @Length(max = 60, message = "签名最多60个字")
    @FormItem(type = FormItemType.TEXTAREA, rows = 2, label = "个性签名", placeholder = "请输入个性签名", maxlength = 60)
    private String sign;
    /**
     * 头像资源id
     */
    @FormItem(type = FormItemType.IMAGE, label = "头像", placeholder = "请上传头像")
    @FormImage(width = "60px", height = "60px", radius = "50%")
    private Long avatarId;
    /**
     * 等级
     */
    @TableField(value = "user_level")
    @FormItem(type = FormItemType.NUMBER, label = "等级", placeholder = "请输入等级", min = 0, max = 6, value = "0")
    @Range(min = 0, max = 6)
    private Integer level;
    /**
     * 经验值
     */
    @FormItem(type = FormItemType.NUMBER, label = "经验值", placeholder = "请输入经验值", min = 0, value = "0")
    @Min(value = 0, message = "经验值不能小于0")
    private Integer exp;
    /**
     * 用户硬币数量
     */
    @JsonSerialize(using = IntegerSerializer.class)
    @FormItem(type = FormItemType.NUMBER, label = "硬币", placeholder = "请输入硬币数量", min = 0, max = 999999, value = "0")
    @Range(min = 0, max = 999999)
    private BigDecimal coin;
    /**
     * 是否禁用 0正常1禁止登录
     */
    @Sensitive
    @FormItem(type = FormItemType.RADIO, label = "是否禁用", dict = "is_status", value = "0")
    private Integer disabled;
    /**
     * 背景图片名称 (不需要返回完整路径)
     */
    @FormItem(type = FormItemType.TEXT, label = "背景图", placeholder = "请输入背景图", value = "默认")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @Length(max = 32, message = "背景名称太长了")
    private String background;
    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @FormItem(type = FormItemType.DATE, label = "注册日期", placeholder = "请选择注册日期", timeFormat = "YYYY-MM-DD")
    private Date registerTime;
    /**
     * 权限列表 角色用ROLE_开头表示
     */
    @Sensitive
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> perms;
    /**
     * 收到的点赞数量
     */
    private Integer likes;
    /**
     * 收到的点踩数量
     */
    private Integer dislikes;
    /**
     * 累计被投币总数量
     * coin = 余额
     * coins = 累计数量
     */
    private Integer coins;
    /**
     * 投稿视频数量
     */
    private Integer videos;
    /**
     * 关注数量
     */
    private Integer follows;
    /**
     * 粉丝数量
     */
    private Integer fans;
    /**
     * 订阅数量
     */
    private Integer subscribes;
    /**
     * 说说数量
     */
    private Integer publishes;
    /**
     * 动态数量
     */
    private Integer dynamics;
    /**
     * 文章数量
     */
    private Integer articles;

    @Override
    @JsonIgnore
    public List<SimpleGrantedAuthority> getAuthorities() {
        return perms.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return Objects.equals(disabled, 0);
    }
    /**
     * 空间访问地址
     */
    public String getUri() {
        return "/space/" + getId();
    }
}