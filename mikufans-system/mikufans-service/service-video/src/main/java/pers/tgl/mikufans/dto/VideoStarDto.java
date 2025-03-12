package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class VideoStarDto implements Serializable {
    /**
     * 视频id
     */
    @NotNull
    private Long videoId;
    /**
     * 增加对该视频收藏的收藏夹id列表
     * 0代表默认收藏夹
     */
    @UniqueElements
    private List<@NotNull Long> addList;
    /**
     * 取消对该视频的收藏夹id列表
     */
    @UniqueElements
    private List<@NotNull Long> delList;
}