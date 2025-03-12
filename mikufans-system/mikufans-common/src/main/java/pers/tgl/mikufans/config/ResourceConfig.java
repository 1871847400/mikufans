package pers.tgl.mikufans.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(value = "app.resource")
public class ResourceConfig {
    /**
     * 基本路径
     */
    private String basePath;
    /**
     * 拼图文件夹路径
     */
    private String captchaPath;
    /**
     * 最大误差值 (0-1)
     */
    private float accuracy = 0.03f;
}