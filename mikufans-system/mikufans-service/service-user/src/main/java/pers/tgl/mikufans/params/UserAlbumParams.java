package pers.tgl.mikufans.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.model.SearchParams;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserAlbumParams extends SearchParams {
    /**
     * 查询的目标用户id
     */
    private Long userId;
    /**
     * 所在位置
     */
    @NotNull
    private Long pid;
}