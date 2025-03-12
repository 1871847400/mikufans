package pers.tgl.mikufans;

import cn.hutool.extra.spring.EnableSpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.config.SocketIOConfig;

import java.io.File;

@SpringBootApplication
@EnableConfigurationProperties(value = {AppConfig.class, SocketIOConfig.class}) //启用自己的配置文件
@EnableCaching //开启缓存功能
@EnableScheduling //开启定时执行功能
@EnableAsync //开启@async注解
@Slf4j //log4j
@EnableTransactionManagement //开启Transactional注解
@EnableSpringUtil //注入hutool SpringUtil
public class MainApplication {
    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        //开启javaCV的debug
        System.setProperty("org.bytedeco.javacpp.logger.debug", "true");
        context = SpringApplication.run(MainApplication.class, args);
        log.info("服务器启动完成！ 耗时:{}秒 运行路径:{}", (System.currentTimeMillis() - startTime)/1000, new File("").getAbsolutePath());
    }
}