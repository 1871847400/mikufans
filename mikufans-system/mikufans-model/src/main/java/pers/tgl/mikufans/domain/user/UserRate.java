package pers.tgl.mikufans.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.jackson.sensitive.Sensitive;
import pers.tgl.mikufans.jackson.sensitive.SensitiveType;

import java.util.Date;

/**
 * 用户评分表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
public class UserRate extends UserBaseEntity {
    /**
     * 目标id
     */
    private Long targetId;
    /**
     * 评分值(1-10)
     */
    private Integer rate;
    /**
     * 评论内容
     */
    @Sensitive(SensitiveType.TERM)
    private String content;
    /**
     * 发布日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getPublishDate() {
        return getCreateTime();
    }
    /**
     * 评论的访问地址
     */
    public String getUri() {
        return "/bangumi/" + targetId +"/rate#" + getId();
    }
}