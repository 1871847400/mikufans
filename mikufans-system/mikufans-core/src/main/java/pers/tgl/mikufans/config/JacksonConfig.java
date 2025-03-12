package pers.tgl.mikufans.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import pers.tgl.mikufans.jackson.AnnotationSerializer;
import pers.tgl.mikufans.jackson.SensitiveFieldProcessModule;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;

/**
 * 配置默认objectMapper(主要用于向前端发送序列化后的对象)
 *      注意：此类不能注入ObjectMapper，否则会出现循环依赖
 */
@Configuration
public class JacksonConfig implements ApplicationRunner {
    @Resource
    @Lazy
    private ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //启动完成后注册java8时间API的支持模块
        objectMapper.findAndRegisterModules();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> {
            // 支持@Sensitive注解
            jacksonObjectMapperBuilder.modules(new SensitiveFieldProcessModule());
            // 所有long字段序列化时转为String，解决前端Number大小限制
            jacksonObjectMapperBuilder.serializerByType(Long.TYPE, ToStringSerializer.instance);
            jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance);
            // 序列化注解本身,默认序列化结果为{}
            jacksonObjectMapperBuilder.serializerByType(Annotation.class, new AnnotationSerializer(objectMapper));
        };
    }
}