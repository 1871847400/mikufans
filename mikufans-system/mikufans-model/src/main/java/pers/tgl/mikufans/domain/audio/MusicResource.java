package pers.tgl.mikufans.domain.audio;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

@Deprecated
@EqualsAndHashCode(callSuper = true)
@Data
public class MusicResource extends UserBaseEntity {
    private String filename;
    private String hashStr;
    @Nullable
    private String musicId;
    private Integer quality;
    private Integer size;
    @Nullable
    private String m3u8;
}
