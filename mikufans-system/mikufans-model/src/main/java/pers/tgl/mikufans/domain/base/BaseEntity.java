package pers.tgl.mikufans.domain.base;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.constraints.Length;
import pers.tgl.mikufans.form.FormItem;
import pers.tgl.mikufans.form.FormItemType;
import pers.tgl.mikufans.jackson.sensitive.Sensitive;
import pers.tgl.mikufans.validator.group.Update;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 数据库实体模型基类
 */
@Data
@FieldNameConstants
public abstract class BaseEntity implements Serializable {
    /**
     * 所有数据库表的主键在Java中使用Long
     * 数据库中使用int或bigint无所谓
     * 传给前端时需要统一转为String
     * 使用自增id或雪花id需在继承后自行覆盖
     */
    @TableId(type = IdType.AUTO)
    @NotNull(groups = Update.class, message = "ID不能为空")
    private Long id;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER, whereStrategy = FieldStrategy.NEVER)
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, whereStrategy = FieldStrategy.NEVER)
    @Sensitive
    private Date updateTime;
    /**
     * 备注
     */
    @Sensitive
    @Length(max = 64, message = "备注长度最多64")
    @FormItem(type = FormItemType.TEXTAREA, rows = 3, label = "备注", placeholder = "请输入备注", maxlength = 64)
    private String remark;
    /**
     * 逻辑删除  0代表未删除 null代表已删除
     * 如果使用1代表已删除会和唯一索引冲突
     * 创建唯一索引时将字段和逻辑删除组合,和NULL组合永远不会造成重复
     * 需要select=true,ES同步要用到
     */
    @TableLogic(value = "0", delval = "null")
    @TableField(fill = FieldFill.INSERT)
    @JsonIgnore
    private Integer deleteFlag;
}