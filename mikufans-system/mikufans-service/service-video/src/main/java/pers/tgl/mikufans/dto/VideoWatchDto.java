package pers.tgl.mikufans.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class VideoWatchDto implements Serializable {
    /**
     * 视频id
     */
    @NotNull
    private Long videoId;
    /**
     * 视频单集id
     */
    @NotNull
    private Long partId;
    /**
     * 观看位置
     */
    @Min(0)
    @NotNull
    private Integer watchPos;
}