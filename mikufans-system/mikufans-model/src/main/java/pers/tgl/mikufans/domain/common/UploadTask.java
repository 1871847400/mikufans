package pers.tgl.mikufans.domain.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.domain.enums.FileUploadStatus;
import pers.tgl.mikufans.jackson.sensitive.Sensitive;

import java.io.File;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@PermFlag(name = "上传任务", group = PermGroup.BUSINESS)
public class UploadTask extends UserBaseEntity {
    /**
     * 原始文件hash
     */
    private String md5;
    /**
     * 媒体类型  image/xx
     */
    private String mediaType;
    /**
     * 原始文件名（包含后缀）
     */
    private String fileName;
    /**
     * 原始文件大小(字节)
     */
    private Integer fileSize;
    /**
     * 存放文件的位置(文件夹)
     */
    @Sensitive
    private String filePath;
    /**
     * 上传状态
     */
    private FileUploadStatus uploadStatus;
    /**
     * 已完成的分片下标,从1开始,0代表未上传任何分片
     * 如果上传完成，chunkCode应该等于chunkCount
     */
    private Integer chunkCode;
    /**
     * 提前规定每个分片大小(字节)
     */
    private Integer chunkSize;
    /**
     * 分片数量
     */
    private Integer chunkCount;
    /**
     * 已经上传的文件大小
     * 如果上传完成 应该等于fileSize
     */
    private Integer uploadSize;
    /**
     * 任务到期时间,会删除源文件
     */
    private Date expireAt;
    /**
     * 是否已经删除源文件
     * 0:未删除 1已删除
     */
    private Integer removed;
    /**
     * 上传任务是否到期
     */
    public boolean isExpired() {
        return new Date().after(expireAt);
    }
    /**
     * 合并后的输出文件
     */
    @JsonIgnore
    public File getOutput() {
        String fileSuffix = FileUtil.getSuffix(getFileName());
        if (StrUtil.isBlank(fileSuffix)) {
            if (mediaType.startsWith("video")) {
                fileSuffix = "mp4";
            } else if (mediaType.startsWith("audio")) {
                fileSuffix = "mp3";
            } else if (mediaType.startsWith("image")) {
                fileSuffix = "jpg";
            }
        }
        return new File(filePath + File.separator + "output." + fileSuffix);
    }
}