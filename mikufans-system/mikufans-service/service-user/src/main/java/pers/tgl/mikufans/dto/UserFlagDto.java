package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import pers.tgl.mikufans.domain.enums.UserFlags;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改用户标记实体
 */
@Data
public class UserFlagDto implements Serializable {
    @NotNull
    private UserFlags flagKey;

    @NotBlank
    @Length(max = 32)
    private String flagValue;
}