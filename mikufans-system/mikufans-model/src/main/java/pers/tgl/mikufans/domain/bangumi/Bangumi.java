package pers.tgl.mikufans.domain.bangumi;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.interfaces.IMessageModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 节目信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@TableName(value = "bangumi", autoResultMap = true)
@PermFlag(name = "节目管理", group = PermGroup.BUSINESS)
public class Bangumi extends UserBaseEntity implements IMessageModel {
    /**
     * 视频id
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long videoId;
    /**
     * 海报id
     */
    private Long posterId;
    /**
     * 更新状态 0未知1更新中2完结
     */
    private Integer releaseStatus;
    /**
     * 每周更新日期 1,2,3,4,5,6,7
     * 0代表未知
     */
    private Integer releaseWeek;
    /**
     * 具体更新时间
     */
    @JsonFormat(pattern = "HH:mm")
    @Nullable
    private LocalTime releaseTime;
    /**
     * 播放季度 0未知 1-4
     */
    private Integer releaseSeason;
    /**
     * 订阅人数
     */
    private Integer subscribe;
    /**
     * 综合评分
     */
    private BigDecimal rate;
    /**
     * 评分人数
     */
    private Integer rateCount;
    /**
     * 首播日期
     */
    @Nullable
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(condition = SqlCondition.LIKE_RIGHT)
    private LocalDate premiere;
    /**
     * 工作人员列表
     */
    private String staff;
    /**
     * 声优列表
     */
    private String voice;
    /**
     * 官方情报
     */
    private String official;
    /**
     * 系列标签,如：第一季
     */
    private String seriesTag;
    /**
     * 关联的节目id列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> seriesIds;
    /**
     * 地区id
     */
    private Long regionId;

    @Override
    public String getUri() {
        return "/bangumi/" + getId();
    }
}