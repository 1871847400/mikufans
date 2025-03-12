package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class UserCommentDto implements Serializable {
    @NotNull
    private Long areaId;

    private Long pid;

    private Long replyId;

    @UniqueElements
    @Size(max = 5, message = "最多上传5张图片")
    private List<@NotNull Long> imageIds;

    @Size(max = 10, message = "最多@10名用户")
    private Map<String, Long> atUsers;

    @NotBlank(message = "请输入评论内容")
    @Length(max = 1000, message = "评论字数最多1000")
    private String content;
}