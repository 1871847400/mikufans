package pers.tgl.mikufans.util;

import lombok.Data;
import pers.tgl.mikufans.domain.enums.VideoQuality;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
public class HlsConvertResult {
    private final List<VideoQuality> qualities;

    public HlsConvertResult() {
        this.qualities = new ArrayList<>(VideoQuality.values().length);
    }

    public void addQuality(VideoQuality quality) {
        this.qualities.add(quality);
    }

    public VideoQuality getMaxLevelQuality() {
        return qualities.stream().max(Comparator.comparingInt(VideoQuality::getLevel))
                .orElse(VideoQuality.SD);
    }
}