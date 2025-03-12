package pers.tgl.mikufans.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HlsSegmentType {
    MPEGTS(".ts"),
    FMP4(".m4s");

    private final String fileSuffix;
}