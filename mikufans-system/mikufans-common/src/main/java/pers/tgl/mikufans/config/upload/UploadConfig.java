package pers.tgl.mikufans.config.upload;

import lombok.Data;
import org.springframework.util.unit.DataSize;

@Data
public class UploadConfig {
    /**
     * 本地保存路径
     */
    private String path;
    /**
     * 大文件分片上传时保存路径
     */
    private String uploadingPath;
    /**
     * 单个文件最大大小
     */
    private DataSize filesize = DataSize.ofGigabytes(1);
    /**
     * 是否加密  暂时只支持视频
     */
    private boolean encrypt = false;
    /**
     * 上传数量限制
     */
    private LimitConfig limit = new LimitConfig();
    /**
     * 转存配置
     */
    private TransferConfig transfer = new TransferConfig();
}