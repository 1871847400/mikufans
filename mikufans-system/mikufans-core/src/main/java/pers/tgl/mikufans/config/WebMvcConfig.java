package pers.tgl.mikufans.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import pers.tgl.mikufans.interceptor.GlobalInterceptor;
import pers.tgl.mikufans.interceptor.OnlineInterceptor;

import javax.annotation.Resource;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private GlobalInterceptor globalInterceptor;

    @Resource
    private OnlineInterceptor onlineInterceptor;

    @Resource
    private AppConfig appConfig;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                //映射resources/static下的静态资源
                .addResourceLocations("classpath:/static/")
                //其它静态资源
                .addResourceLocations("file:" + appConfig.getResource().getBasePath() + "/");

        // 解决 SWAGGER 404报错
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalInterceptor)
                .addPathPatterns("/**"); //针对所有路径
        registry.addInterceptor(onlineInterceptor)
                .addPathPatterns("/**");
    }

    /**
     * 配置内容协商设置
     *  默认会以客户端发送的accept来决定
     * @param configurer
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
//        configurer.defaultContentTypeStrategy(new FixedContentNegotiationStrategy(MediaType.APPLICATION_JSON));
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    }
}