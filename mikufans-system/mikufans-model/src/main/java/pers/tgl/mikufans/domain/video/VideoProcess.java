package pers.tgl.mikufans.domain.video;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.model.VideoPreprocess;

import java.io.File;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@TableName(autoResultMap = true)
public class VideoProcess extends UserBaseEntity {
    /**
     * 视频id
     */
    private Long videoId;
    /**
     * 分集id
     */
    private Long partId;
    /**
     * 资源id
     */
    private Long resId;
    /**
     * 视频文件路径
     */
    private String filepath;
    /**
     * 总进度 (0-100)
     */
    private Integer progress;
    /**
     * 阶段
     */
    private Integer stage;
    /**
     * 加工参数
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private VideoPreprocess params;
    /**
     * 处理结果 0等待 1成功 2失败
     */
    private Integer result;
    /**
     * 如果失败，失败的原因
     */
    private String errorMsg;

    @JsonIgnore
    public File getVideoFile() {
        return new File(filepath);
    }
}