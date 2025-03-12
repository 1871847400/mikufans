package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserRateDto implements Serializable {
    /**
     * 目标id
     */
    @NotNull
    private Long targetId;
    /**
     * 评分
     */
    @NotNull(message = "请选择分数")
    @Range(min = 1, max = 10)
    private Integer rate;
    /**
     * 评论内容
     */
    @Length(max = 800, message = "评论最多800个字")
    private String content;
}