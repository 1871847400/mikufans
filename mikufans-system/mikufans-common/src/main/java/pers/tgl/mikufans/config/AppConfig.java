package pers.tgl.mikufans.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pers.tgl.mikufans.config.upload.UploadConfig;
import pers.tgl.mikufans.config.video.VideoConfig;
import pers.tgl.mikufans.consts.FileMediaType;
import pers.tgl.mikufans.consts.TokenType;

import java.io.Serializable;
import java.util.Map;

/**
 * 自定义配置文件
 */
@ConfigurationProperties(value = "app")
@EnableConfigurationProperties(value = { ResourceConfig.class, VideoConfig.class, OssConfig.class })
@Data
public class AppConfig implements Serializable {
    /**
     * 令牌配置
     */
    private Map<TokenType, TokenConfig> token;
    /**
     * 文件类型 : 上传配置
     */
    private Map<FileMediaType, UploadConfig> upload;
    /**
     * 自定义属性
     */
    private Map<String, Object> custom;
    /**
     * 资源配置文件
     */
    private ResourceConfig resource;
    /**
     * 视频配置
     */
    private VideoConfig video;
    /**
     * 阿里云oss
     */
    private OssConfig oss;
}