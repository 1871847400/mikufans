package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserDynamicDto implements Serializable {
    @Null(groups = Create.class)
    @NotNull(groups = Update.class)
    private Long id;
    /**
     * 可见范围
     */
    @Range(min = 0, max = 1)
    @NotNull(groups = Create.class)
    private Integer visible;
    /**
     * 是否定时发布
     */
    @Range(min = 0, max = 1)
    @NotNull(groups = Create.class)
    private Integer publishFlag;
    /**
     * 定时发布时间
     */
    @Future(message = "定时发布时间只能在2小时以后", groups = Create.class)
    private Date publishTime;
    /**
     * 评论区dto
     */
    @Valid
    @NotNull(groups = Create.class)
    private CommentAreaDto commentArea;
}