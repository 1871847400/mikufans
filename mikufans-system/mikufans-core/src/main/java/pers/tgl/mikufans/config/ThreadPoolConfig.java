package pers.tgl.mikufans.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 线程池配置
 * spring容器自带一个applicationTaskExecutor, @Bean会覆盖默认的
 */
//@Configuration
public class ThreadPoolConfig implements AsyncConfigurer {
    @ConditionalOnMissingBean
    public ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //设置核心线程数
        executor.setCorePoolSize(5);
        //设置最大线程数
        executor.setMaxPoolSize(10);
        //设置队列大小
        executor.setQueueCapacity(100);
        //设置线程池中线程的名称前缀
        executor.setThreadNamePrefix("CustomAsyncTaskExecutor-");
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * 配置@Async使用的线程池
     */
    @Override
    public Executor getAsyncExecutor() {
        return executor();
    }
}