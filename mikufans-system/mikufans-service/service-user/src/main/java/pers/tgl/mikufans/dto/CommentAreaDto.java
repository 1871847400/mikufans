package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import pers.tgl.mikufans.domain.enums.CommentFlag;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

import static pers.tgl.mikufans.consts.Consts.USER_MAX_LEVEL;
import static pers.tgl.mikufans.consts.Consts.USER_MIN_LEVEL;

@Data
public class CommentAreaDto implements Serializable {
    @NotNull(groups = Update.class)
    @Null(groups = Create.class)
    private Long id;

    @NotNull(groups = Create.class)
    private CommentFlag commentFlag;

    @NotNull(groups = Create.class)
    @Range(min = USER_MIN_LEVEL, max = USER_MAX_LEVEL)
    private Integer userLevel;
}