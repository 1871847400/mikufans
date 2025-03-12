package pers.tgl.mikufans.domain.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.domain.enums.NoticeType;
import pers.tgl.mikufans.form.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@PermFlag(name = "系统通知模板", group = PermGroup.SYSTEM)
@Form(labelPosition = "right", labelWidth = "100px", labelSuffix = ":")
public class SysNotice extends SystemBaseEntity {
    /**
     * 通知类型
     */
    @NotNull(message = "请选择通知类型")
    @FormItem(type = FormItemType.SELECT, label = "通知类型", placeholder = "请选择通知类型", required = Scope.BOTH)
    @FormSelect(provider = NoticeType.class, defaultFirstOption = true)
    private NoticeType noticeType;
    /**
     * 通知的标题
     */
    @NotBlank(message = "请输入标题")
    @FormItem(type = FormItemType.TEXT, label = "通知标题", placeholder = "请输入通知标题", required = Scope.BOTH)
    private String title;
    /**
     * 通知的模板内容
     */
    @NotBlank(message = "请输入模板内容")
    @FormItem(type = FormItemType.TEXTAREA, label = "通知模板", placeholder = "请输入通知模板", required = Scope.BOTH)
    private String template;
    /**
     * 默认跳转链接
     */
    @FormItem(type = FormItemType.TEXT, label = "默认链接", placeholder = "请输入默认链接")
    private String defUrl;
    /**
     * 启用时间
     */
    @FormItem(type = FormItemType.DATE, label = "启用时间", placeholder = "请选择启用时间", value = "now()")
    private Date enableTime;
    /**
     * 到期时间
     */
    @Nullable
    @FormItem(type = FormItemType.DATE, label = "到期时间", placeholder = "请选择到期时间")
    private Date expireTime;
}