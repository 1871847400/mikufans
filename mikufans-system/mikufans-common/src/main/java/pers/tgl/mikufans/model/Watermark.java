package pers.tgl.mikufans.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 水印
 * 无论是图片水印还是文字水印,前端统一使用Canvas生成图片
 */
@Data
public class Watermark implements Serializable {
    /**
     * base64图片数据 (png)
     */
    @NotBlank
    private String data;
    /**
     * 横坐标 相对于左上角
     */
    @NotNull
    private Float x;
    /**
     * 纵坐标 相当于左上角
     */
    @NotNull
    private Float y;
}