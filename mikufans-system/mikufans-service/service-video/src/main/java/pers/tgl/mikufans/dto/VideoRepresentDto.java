package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class VideoRepresentDto implements Serializable {
    /**
     * 视频id
     */
    @NotNull
    private Long videoId;
    /**
     * 原因
     */
    @Length(max = 50, message = "原因最多50字")
    private String reason;
}