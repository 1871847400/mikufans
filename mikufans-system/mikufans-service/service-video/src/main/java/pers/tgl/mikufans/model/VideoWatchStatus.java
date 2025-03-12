package pers.tgl.mikufans.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 监测在线用户的观看状态
 */
@Data
public class VideoWatchStatus implements Serializable {
    private Long userId;
    private Long videoId;
    private Long partId;
    private Integer watchPos;
    private Long updateTime;
}