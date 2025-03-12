package pers.tgl.mikufans.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云oss配置
 * <a href="https://help.aliyun.com/zh/oss/developer-reference/oss-java-configure-access-credentials">参考地址</a>
 */
@Data
@ConfigurationProperties(value = "app.oss")
public class OssConfig {
    private String endpoint;
    /**
     * 地区，如：cn-hangzhou
     */
    private String region;
    private String accessKeyId;
    private String accessKeySecret;
    /**
     * 桶名称,不存在会自动创建
     */
    private String bucketName;
}