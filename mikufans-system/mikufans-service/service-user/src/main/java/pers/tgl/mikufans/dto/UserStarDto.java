package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

@Data
public class UserStarDto implements Serializable {
    /**
     * 收藏夹id
     */
    @Null(groups = Create.class)
    @NotNull(groups = Update.class)
    private Long id;
    /**
     * 收藏夹名称
     */
    @NotBlank(message = "请输入收藏夹名称")
    @Length(max = 16, message = "名称太长了")
    private String starName;
    /**
     * 收藏夹封面id
     */
    private Long coverId;
    /**
     * 收藏夹简介
     */
    @Length(max = 100, message = "简介太长了")
    private String intro;
    /**
     * 是否公开
     */
    @NotNull
    @Range(min = 0, max = 1)
    private Integer visible;
}