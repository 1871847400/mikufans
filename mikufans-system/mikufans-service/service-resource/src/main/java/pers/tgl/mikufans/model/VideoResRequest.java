package pers.tgl.mikufans.model;

import lombok.Data;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.video.VideoResource;

import java.io.Serializable;

/**
 * 存放在Redis中的用户视频请求
 */
@Deprecated
@Data
public class VideoResRequest implements Serializable {
    /**
     * 客户端ip
     */
    private String ip;
    /**
     * 请求的分集id
     */
    private Long partId;
    /**
     *
     */
    private VideoResource resource;
    /**
     * 用户id(可能是游客未登录)
     */
    @Nullable
    private Long userId;
}