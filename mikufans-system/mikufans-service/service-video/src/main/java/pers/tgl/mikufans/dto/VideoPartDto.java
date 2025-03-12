package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import pers.tgl.mikufans.domain.common.ImageResource;
import pers.tgl.mikufans.domain.common.UploadTask;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.model.VideoPreprocess;
import pers.tgl.mikufans.validator.db.DBExists;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.List;

@Data
public class VideoPartDto implements Serializable {
    @NotNull(groups = Update.class)
    @Null(groups = Create.class)
    @DBExists(value = VideoPart.class, checkUserId = true)
    private Long id;
    /**
     * 分集名称
     */
    @Length(max = 32, message = "分集名称最长32")
    @NotBlank(message = "分集名称不能空", groups = Create.class)
    private String partName;
    /**
     * 视频id
     * 注意：创建时会使用无效值占位
     */
    @NotNull(groups = Create.class)
    @Null(groups = Update.class)
    private Long videoId;
    /**
     * 文件上传任务id
     */
    @NotNull(groups = Create.class)
    @DBExists(value = UploadTask.class, message = "上传任务无效")
    private Long taskId;
    /**
     * 封面
     */
    @DBExists(value = ImageResource.class, ignores = "0")
    private Long bannerId;
    /**
     * 排序值,
     */
    @Min(value = 1)
    private Integer sort;
    /**
     * 字幕列表
     */
    private List<SubtitleFile> subtitles;
    /**
     * 预处理配置,创建时才能设置
     */
    @Null(groups = Update.class)
    @Valid
    private VideoPreprocess preprocess;
    /**
     * 是否为追加分集,只能由内部设置
     */
    @Null(message = "非法参数")
    private Integer append;
}