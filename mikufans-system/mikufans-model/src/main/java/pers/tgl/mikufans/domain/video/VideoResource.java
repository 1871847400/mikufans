package pers.tgl.mikufans.domain.video;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.consts.TransferMode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.jackson.sensitive.Sensitive;
import pers.tgl.mikufans.model.MediaInfo;
import pers.tgl.mikufans.util.MyUtils;

import java.time.Duration;

/**
 * 视频资源表
 * @TableName video_resource
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="video_resource", autoResultMap = true)
@Data
@PermFlag(name = "视频资源", group = PermGroup.BUSINESS)
public class VideoResource extends UserBaseEntity {
    /**
     * md5 预处理前
     */
    private String md5;
    /**
     * 媒体类型 video/xxx 预处理前
     */
    private String mediaType;
    /**
     * 文件总大小(bytes) 预处理前
     */
    private Integer fileSize;
    /**
     * 视频时长(ms) 预处理后
     */
    private Integer duration;
    /**
     * 本地存储路径
     */
    @Sensitive
    private String localPath;
    /**
     * 转存路径
     */
    @Sensitive
    private String transferPath;
    /**
     * 转存模式
     */
    @Sensitive
    private TransferMode transferMode;
    /**
     * 0可用 1不可用
     */
    private Integer pending;
    /**
     * 最高画质等级
     */
    private Integer qualityLevel;
    /**
     * 预处理前的元数据(很大,一般不获取)
     */
    @Sensitive
    @TableField(select = false, typeHandler = JacksonTypeHandler.class)
    private MediaInfo mediaInfo;
    /**
     * 格式化持续时长
     */
    public String getDurationFormat() {
        return MyUtils.formatDuration(Duration.ofMillis(getDuration()));
    }
}