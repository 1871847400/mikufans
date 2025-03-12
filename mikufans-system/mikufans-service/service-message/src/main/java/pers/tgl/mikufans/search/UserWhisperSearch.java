package pers.tgl.mikufans.search;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.model.SearchParams;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserWhisperSearch extends SearchParams {
    /**
     * 目标用户id
     */
    @NotNull
    private Long targetId;
    /**
     * 限制已读状态
     */
    private Integer readFlag;
}