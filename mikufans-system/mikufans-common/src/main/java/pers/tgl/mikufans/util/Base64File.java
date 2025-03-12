package pers.tgl.mikufans.util;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

/**
 * 用于方便传输文件数据
 * 如果直接传输文件(MultipartFile)会有限制,必须单独传输文件
 */
@Data
public class Base64File implements Serializable {
    /**
     * 原始文件名称
     */
    @NotBlank
    private String filename;
    /**
     * 文件大小(字节)
     */
    @NotNull
    @Positive
    private Integer filesize;
    /**
     * 媒体类型
     */
    @NotBlank
    private String mediaType;
    /**
     * base64数据
     */
    @NotBlank
    private String data;
}