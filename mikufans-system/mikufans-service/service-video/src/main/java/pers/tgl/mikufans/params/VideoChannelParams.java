package pers.tgl.mikufans.params;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class VideoChannelParams {
    /**
     * 限制数量
     */
    @Min(0)
    private Integer size;
    /**
     * 查找子分区
     */
    private boolean child = false;
}