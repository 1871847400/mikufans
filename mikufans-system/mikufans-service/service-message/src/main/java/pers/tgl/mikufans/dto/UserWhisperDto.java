package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.validator.db.DBExists;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserWhisperDto implements Serializable {
    @NotBlank(message = "请输入发送内容")
    @Length(max = 1024, message = "字数超过限制")
    private String message;

    @Range(min = 1, max = 2, message = "无效的消息类型")
    private Integer msgType;

    @NotNull(message = "必须有目标用户")
    @DBExists(value = User.class, message = "用户不存在")
    private Long receiverId;
}