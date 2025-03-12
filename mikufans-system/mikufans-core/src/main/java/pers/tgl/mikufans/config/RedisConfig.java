package pers.tgl.mikufans.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.*;
import pers.tgl.mikufans.handler.CustomRedisCacheManager;
import pers.tgl.mikufans.jackson.MediaTypeModule;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 配置redis存入的key和value序列化及反序列化
 */
@Configuration
@Slf4j
public class RedisConfig {
    /**
     * 默认操作redis模板
     * 序列化时存储为普通的JSON字符串，不带@class
     * 反序列化需要手动转换
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        /**
         * 创建一个专用于Redis序列化的ObjectMapper
         */
        ObjectMapper objectMapper = new ObjectMapper();
        /**
         * 修改可视化属性：类型 : 程度
         *  类型:
         *      例如设置PropertyAccessor.ALL表示针对：get/set/is方法以及字段
         *  程度:
         *      例如Visibility.ANY 代表无论是public还是private等修饰都会被序列化/反序列化
         *      Visibility.PROTECTED_AND_PUBLIC 代表只有protect和public才会被序列化
         */
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        // get,set,is方法不参与序列化
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
//         重要：对序列化后的类型进行标注(在json字符串中)，以方便反序列化时准确找到对应的java类型
//        但如果标注类型,那么类文件不能变动,自定义转换器也必须手动加上类型
//        objectMapper.activateDefaultTyping(
//                LaissezFaireSubTypeValidator.instance,
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                JsonTypeInfo.As.PROPERTY);

        // 添加模块(兼容java8时间api)，前提是pom有引入
        objectMapper.findAndRegisterModules();

        objectMapper.registerModule(new MediaTypeModule());

        // jackson序列化器 对value进行序列化, 反序列化会转为linkedHashmap
//        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer
//                = new Jackson2JsonRedisSerializer<>(Object.class);
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//        template.setValueSerializer(jackson2JsonRedisSerializer);
//        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 字符串序列化器，对key进行序列化和反序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // json序列化器,序列化时转为json串,反序列化时不做额外操作
        RedisSerializer<Object> valueRedisSerializer = new RedisSerializer<Object>() {
            @Override
            public byte[] serialize(Object object) throws SerializationException {
                try {
                    if (object instanceof String) {
                        return object.toString().getBytes(StandardCharsets.UTF_8);
                    } else {
                        //如果序列化字符串,会带上多余的双引号
                        return objectMapper.writeValueAsBytes(object);
                    }
                } catch (JsonProcessingException e) {
                    throw new SerializationException(e.getMessage());
                }
            }
            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                return bytes == null ? null : new String(bytes);
            }
        };
        template.setValueSerializer(valueRedisSerializer);
        template.setHashValueSerializer(valueRedisSerializer);

        // 补充默认配置
        template.afterPropertiesSet();

        return template;
    }

    /**
     * 自定义缓存控制器, @Cacheable会使用redis作为缓存
     * 需要使用会加入@class的序列化器GenericJackson2JsonRedisSerializer，否则反序列化无法自动完成！
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, RedisTemplate<String, Object> redisTemplate) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        //设置类型标注@class
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        objectMapper.findAndRegisterModules();
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .entryTtl(Duration.ofHours(1)); // 设置默认缓存有效期一小时
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        return new CustomRedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }

    @Bean
    public RedisMessageListenerContainer container(LettuceConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}