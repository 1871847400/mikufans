package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.MediaType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

@Data
public class UploadTaskDto implements Serializable {
    /**
     * 文件的原始名称
     */
    @NotBlank
    @Length(max = 100, message = "文件名过长")
    private String fileName;
    /**
     * 媒体类型
     */
    @NotNull
    private MediaType mediaType;
    /**
     * 文件hash值
     */
    @NotBlank
    @Length(min = 32, max = 32)
    private String md5;
    /**
     * 文件大小(字节)
     */
    @NotNull
    @Positive
    private Integer fileSize;
    /**
     * 分片大小(字节)
     * 最少 128 KB  最大 2 MB
     */
    @NotNull
    @Range(min = 1024 * 128, max = 1024 * 1024 * 2)
    private Integer chunkSize;

    public int getChunkCount() {
        return (int) Math.ceil((double) fileSize / chunkSize);
    }
}