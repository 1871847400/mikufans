package pers.tgl.mikufans.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class VideoFlagDto implements Serializable {
    @NotBlank
    private String videoId;
    private Long partId;
    @NotBlank
    private String flagName;
    @NotNull
    private String flagValue;
}