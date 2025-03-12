package pers.tgl.mikufans.vo;

import lombok.Data;

@Data
public class CaptureImageVo {
    /**
     * 区分请求id
     */
    private String uuid;
    /**
     * base64
     */
    private String image;
}