package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import pers.tgl.mikufans.domain.common.ImageResource;
import pers.tgl.mikufans.validator.db.DBExists;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UserPublishDto {
    @NotNull(groups = Update.class)
    @Null(groups = Create.class)
    private Long id;
    /**
     * 说说标题
     */
    @Length(max = 20, message = "标题不能超过20个字")
    private String title;
    /**
     * 说说内容
     */
    @NotBlank(message = "请填写正文")
    @Length(max = 2000, message = "正文字数太多了")
    private String content;
    /**
     * 附带图片列表
     */
    @Size(max = 20, message = "最多附加10张图片")
    private List<@DBExists(value = ImageResource.class, message = "包含不存在的图片") Long> imgIds;
    /**
     * 动态信息
     */
    @Valid
    @NotNull(groups = Create.class)
    private UserDynamicDto dynamic;
}