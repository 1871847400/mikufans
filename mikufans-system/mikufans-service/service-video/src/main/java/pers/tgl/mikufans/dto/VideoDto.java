package pers.tgl.mikufans.dto;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UniqueElements;
import pers.tgl.mikufans.domain.common.ImageResource;
import pers.tgl.mikufans.domain.enums.VideoType;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoChannel;
import pers.tgl.mikufans.validator.db.DBExists;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.validation.groups.ConvertGroup;
import java.util.List;
import java.util.Objects;

import static pers.tgl.mikufans.consts.Consts.*;

/**
 * 创建或更新视频的数据模型
 */
@Data
public class VideoDto {
    /**
     * 视频id
     */
    @NotNull(groups = Update.class)
    @Null(groups = Create.class)
    @DBExists(value = Video.class, checkUserId = true)
    private Long id;
    /**
     * 分类 已设置@TableField后续修改不会生效
     */
    @NotNull(groups = Create.class, message = "必须指定分类")
    private VideoType type;
    /**
     * 分区  番剧电影等设置为0即可
     */
    @NotNull(groups = Create.class, message = "请选择分区")
    @DBExists(value = VideoChannel.class, ignores = "0", message = "不存在的分区")
    private Long channelId;
    /**
     * 视频标题
     */
    @NotBlank(message = "请填写标题", groups = Create.class)
    @Length(min = VIDEO_TITLE_MIN_LENGTH, max = VIDEO_TITLE_MAX_LENGTH, message = "标题过短或过长")
    private String title;
    /**
     * 视频简介
     */
    @Length(max = VIDEO_INTRO_MAX_LENGTH, message = "简介字数太长")
    private String intro;
    /**
     * PC端封面
     */
    @NotNull(message = "请选择封面", groups = Create.class)
    @DBExists(ImageResource.class)
    private Long bannerId;
    /**
     * 手机端封面
     */
    @DBExists(value = ImageResource.class, ignores = "0")
    private Long mBannerId;
    /**
     * 是否为转载
     */
    @NotNull(groups = Create.class, message = "请选择是否转载")
    @Range(min = 0, max = 1)
    private Integer republish;
    /**
     * 转载地址
     */
    @Length(max = 200)
    private String sourceUrl;
    /**
     * 视频标签列表
     */
    @Size(max = VIDEO_TAG_MAX_COUNT, message = "视频标签超过数量限制")
    @UniqueElements
    @NotNull(groups = Create.class)
    private List<@NotBlank @Length(max = VIDEO_TAG_MAX_LENGTH, message = "标签长度超过限制") String> tags;
    /**
     * 视频观看要求等级  必须设置<=自己当前的等级
     */
    @Range(min = USER_MIN_LEVEL, max = USER_MAX_LEVEL)
    private Integer userLevel;
    /**
     * 前端传videoId,可以暂时设置为其它任意有效值
     */
    @Size(min = 1, message = "请至少上传一个视频", groups = Create.class)
    @Size(max = VIDEO_PART_MAX_COUNT, message = "分集数量超过最大限制", groups = Create.class)
    @NotNull(groups = Create.class)
    private List<@NotNull @Valid @ConvertGroup(from = Update.class, to = Create.class) VideoPartDto> createParts;
    /**
     * 更新的分集
     */
    @Size(max = 0, message = "创建时不允许更新", groups = Create.class)
    private List<@NotNull @Valid VideoPartDto> updateParts;
    /**
     * 如果有,代表创建节目
     */
    @Valid
    private BangumiDto bangumi;
    /**
     * 动态设置
     */
    @Valid
    @NotNull(groups = Create.class)
    private UserDynamicDto dynamic;

    @AssertFalse(message = "转载视频请填写转载地址")
    public boolean isError() {
        return Objects.equals(republish, 1) && StrUtil.isBlank(getSourceUrl());
    }

    @AssertFalse(message = "请填写番剧信息", groups = Create.class)
    public boolean isError2() {
        return getType() != null && getType().isBangumi() && getBangumi() == null;
    }
}