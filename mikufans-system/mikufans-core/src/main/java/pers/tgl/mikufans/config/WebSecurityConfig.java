package pers.tgl.mikufans.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import pers.tgl.mikufans.filter.TokenFilter;
import pers.tgl.mikufans.handler.MyPermissionEvaluator;
import pers.tgl.mikufans.handler.MySecurityExceptionHandler;

import javax.annotation.Resource;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {
    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private MySecurityExceptionHandler mySecurityExceptionHandler;

    @Resource
    private TokenFilter tokenFilter;

    @Resource
    private MyPermissionEvaluator permissionEvaluator;

    @Bean
    public SecurityFilterChain web(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeRequests()
                .mvcMatchers("/admin/login/**").permitAll()
                .mvcMatchers("/admin/**").authenticated()
                .mvcMatchers("/auth/**").permitAll()
                .mvcMatchers(HttpMethod.GET, "/**").permitAll()
                .anyRequest()
                .authenticated()
                .expressionHandler(defaultWebSecurityExpressionHandler())
                .and()
                .userDetailsService(userDetailsService)
                .exceptionHandling()
                .authenticationEntryPoint(mySecurityExceptionHandler)
                .accessDeniedHandler(mySecurityExceptionHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //不使用JSESSIONID
                .and()
                .addFilterAfter(tokenFilter, ExceptionTranslationFilter.class)
                .csrf().disable() //禁用防止跨域
                .build();
    }

    /**
     * 配置PasswordEncoder,会自动为生成的hash串加上方式前缀,以支持校验不同类型的密码
     * 例如：{bcrypt}$2a$10$lrEB9JYaSm   {noop}123456
     */
    @ConditionalOnMissingBean
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用 spring security 提供的默认passwordEncoder进行加密(默认为BCrypt加密方式)
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }
}