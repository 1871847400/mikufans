package pers.tgl.mikufans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 单纯 存储了前端要用到的点赞数据
 */
@Data
public class LikeStatus implements Serializable {
    private Long id;
    private Integer likeVal;
    private Integer likes;
    private Integer dislikes;
}