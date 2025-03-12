package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.validator.db.DBExists;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDynamicShareDto {
    /**
     * 转发id
     */
    @NotNull
    @DBExists(value = UserDynamic.class, message = "这条动态已消失")
    private Long shareId;
    /**
     * 转发理由
     */
    @NotBlank(message = "请输入转发理由")
    @Length(max = 1000, message = "最多1000字")
    private String shareReason;
}