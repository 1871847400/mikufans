package pers.tgl.mikufans.config.video;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Data
@ConfigurationProperties("app.video.coin")
public class CoinConfig {
    /**
     * 初始硬币数量
     */
    private BigDecimal initialCount = BigDecimal.ZERO;
    /**
     * 视频作者获取硬币的比例
     */
    private float harvest = 0.1F;
}
