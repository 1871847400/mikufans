package pers.tgl.mikufans.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.consts.VideoStatus;
import pers.tgl.mikufans.domain.enums.VideoType;
import pers.tgl.mikufans.model.SearchParams;
import pers.tgl.mikufans.search.VideoSearchSort;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoUploadParams extends SearchParams {
    /**
     * 限制状态
     */
    private VideoStatus status;
    /**
     * 视频类型
     */
    private VideoType videoType;
    /**
     * 排序字段
     */
    @NotNull
    private VideoSearchSort sort = VideoSearchSort.TIME;
    /**
     * 是否升序
     */
    @NotNull
    private boolean asc = false;
}