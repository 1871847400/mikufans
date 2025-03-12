package pers.tgl.mikufans.domain.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.jackson.sensitive.Sensitive;
import pers.tgl.mikufans.jackson.sensitive.SensitiveType;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@PermFlag(name = "用户说说", group = PermGroup.BUSINESS)
@TableName(autoResultMap = true)
public class UserPublish extends UserBaseEntity {
    /**
     * 说说标题
     */
    private String title;
    /**
     * 文章内容
     */
    @Sensitive(SensitiveType.TERM)
    private String content;
    /**
     * 图片列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> imgIds;
}