package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UniqueElements;
import pers.tgl.mikufans.domain.bangumi.Bangumi;
import pers.tgl.mikufans.domain.bangumi.BangumiStyle;
import pers.tgl.mikufans.validator.db.DBExists;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 依托于VideoDto
 */
@Data
public class BangumiDto implements Serializable {
    @NotNull(groups = Update.class)
    @Null(groups = Create.class)
    private Long id;

    /**
     * 只能由后端设置
     */
    @Null
    private Long videoId;

    @NotNull(message = "请上传海报图", groups = Create.class)
    private Long posterId;

    @Range(min = 0, max = 2)
    @NotNull(message = "请选择更新状态", groups = Create.class)
    private Integer releaseStatus;

    @Range(min = 0, max = 7)
    private Integer releaseWeek;

    private LocalTime releaseTime;

    @Range(min = 0, max = 4)
    private Integer releaseSeason;

    private LocalDate premiere;

    @Length(max = 512, message = "STAFF信息太长了")
    private String staff;

    @Length(max = 512, message = "声优信息太长了")
    private String voice;

    @Length(max = 512, message = "官方情报太长了")
    private String official;

    @UniqueElements(message = "风格重复了")
    @Size(max = 10, message = "最多选择10种风格")
    private List<@NotNull @DBExists(BangumiStyle.class) Long> styles;

    @Length(max = 32)
    private String seriesTag;

    @UniqueElements
    @Size(max = 10)
    private List<@DBExists(value = Bangumi.class, checkUserId = true) @NotBlank String> seriesIds;

    @NotNull(groups = Create.class, message = "请选择发行地区")
    private Long regionId;
}