package pers.tgl.mikufans.domain.user;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

import java.util.Date;

/**
 * 用户相册表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
public class UserAlbum extends UserBaseEntity {
    /**
     * 所在相册id
     */
    private Long pid;
    /**
     * 类型 1图片 2相册
     */
    private Integer category;
    /**
     * 名称
     */
    @TableField(condition = SqlCondition.LIKE_RIGHT)
    private String title;
    /**
     * 图片id
     */
    private Long imgId;
    /**
     * 排序值
     */
    private Integer sort;
    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date getCreateDate() {
        return getCreateTime();
    }
}