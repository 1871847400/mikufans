package pers.tgl.mikufans.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class UserVideoStats implements Serializable {
    /**
     * 视频投稿总数
     */
    private Integer uploadTotal;
    /**
     * 正在上传的数量
     */
//    private Integer uploadingCount;
    /**
     * 视频分类id : 投稿数量
     */
    private Map<Long, Integer> uploadCountMap;
}
