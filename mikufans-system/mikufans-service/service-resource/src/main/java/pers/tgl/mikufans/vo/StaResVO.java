package pers.tgl.mikufans.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import pers.tgl.mikufans.util.LongSerializer;

import java.io.Serializable;

@Data
public class StaResVO implements Serializable {
    private Long id;
    private String mimeType;
    @JsonSerialize(using = LongSerializer.class)
    private Long size;
    private Long userId;
}