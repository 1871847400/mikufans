package pers.tgl.mikufans.config;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties(value = "socketio")
@Data
public class SocketIOConfig {
    /**
     * 主机地址
     */
    private String host;
    /**
     * 连接端口
     */
    private Integer port;
    /**
     * 最大每帧长度
     */
    private Integer maxFramePayloadLength;
    /**
     * http交互最大内容长度
     */
    private Integer maxHttpContentLength;
    /**
     * socket 连接数大小,如果只监听一个端口设置为1
     */
    private int bossCount;

    private int workCount;

    private boolean allowCustomRequests;
    /**
     * HTTP握手升级为ws协议的超时时间 毫秒
     */
    private int upgradeTimeout;
    /**
     * ping 消息超时时间 毫秒
     */
    private int pingTimeout;
    /**
     * ping消息间隔 毫秒
     */
    private int pingInterval;
    /**
     * Access-Control-Allow-Origin
     */
    private String origin;

    @Bean
    public SocketIOServer socketIOServer() {
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setTcpNoDelay(true);
        socketConfig.setSoLinger(0);
        Configuration config = new Configuration();
        config.setSocketConfig(socketConfig);
        config.setHostname(host);
        config.setPort(port);
        config.setBossThreads(bossCount);
        config.setWorkerThreads(workCount);
        config.setMaxFramePayloadLength(maxFramePayloadLength);
        config.setMaxHttpContentLength(maxHttpContentLength);
        config.setAllowCustomRequests(allowCustomRequests);
        config.setUpgradeTimeout(upgradeTimeout);
        config.setPingTimeout(pingTimeout);
        config.setPingInterval(pingInterval);
//        config.setEnableCors(false);
//        config.setOrigin(origin);
        return new SocketIOServer(config);
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}