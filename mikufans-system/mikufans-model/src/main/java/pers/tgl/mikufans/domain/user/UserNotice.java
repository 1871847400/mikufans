package pers.tgl.mikufans.domain.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.enums.NoticeType;
import pers.tgl.mikufans.jackson.sensitive.Sensitive;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(autoResultMap = true)
public class UserNotice extends UserBaseEntity {
    /**
     * 通知类型
     */
    private NoticeType noticeType;
    /**
     * 通知id
     * 主要对于公告类通知才有用
     * 其他通知一律为0
     */
    private Long noticeId;
    /**
     * 模板内容参数
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    @Sensitive
    private List<String> params;
    /**
     * 自定义链接，如果没有会使用模板的默认链接
     */
    private String url;
    /**
     * 是否已读
     */
    private Integer readFlag;
    /**
     * 是否隐藏
     */
    private Integer hidden;
}