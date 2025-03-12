package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pers.tgl.mikufans.domain.user.UserAlbum;
import pers.tgl.mikufans.validator.db.DBExists;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
public class UserAlbumDto {
    @Null(groups = Create.class)
    @NotNull(groups = Update.class)
    private Long id;

    @NotNull(groups = Create.class, message = "请指定图片id")
    private Long imgId;

    @Length(max = 16, message = "标题长度最多16")
    private String title;

    @NotNull
    @Range(min = 1, max = 2)
    private Integer category;

    @DBExists(value = UserAlbum.class, checkUserId = true)
    private Long pid;
}