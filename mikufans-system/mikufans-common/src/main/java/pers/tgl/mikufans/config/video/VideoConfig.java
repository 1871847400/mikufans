package pers.tgl.mikufans.config.video;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Data
@ConfigurationProperties("app.video")
@EnableConfigurationProperties(CoinConfig.class)
public class VideoConfig {
    /**
     * 投币的配置
     */
    private CoinConfig coin;
}