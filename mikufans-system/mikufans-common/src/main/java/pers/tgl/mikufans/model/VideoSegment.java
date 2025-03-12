package pers.tgl.mikufans.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoSegment implements Serializable {
    /**
     * 偏移s
     */
    private Float offset;
    /**
     * 时长s
     */
    private Float length;
}
