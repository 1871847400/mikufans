package pers.tgl.mikufans.config;

import lombok.Data;

import java.time.Duration;

@Data
public class TokenConfig {
    /**
     * token有效时长
     */
    private Duration duration;
    /**
     * token对称密码
     */
    private String secret;
}
