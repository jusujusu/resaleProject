package com.example.back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * FileName    : RedisConfig
 * Since       : 26. 4. 1.
 * Dsecription  : Redis 설정을 담당하는 클래스
 */
@Configuration
public class RedisConfig {

    @Value("${REDIS_HOST}")
    private String host;

    @Value("${REDIS_PORT}")
    private int port;


    /*
     * Redis 연결을 위한 팩토리 생성 (Lettuce 방식)
     * */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // application.properties에서 가져온 host, port로 연결
        return new LettuceConnectionFactory(host, port);
    }


    /*
     * 일반적인 객체 저장용 RedisTemplate 설정
     * */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // Key와 Value를 모두 String 형식으로 직렬화 (가독성 및 호환성)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }


    /*
    * 문자열 전용 RedisTemplate
    * */
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
        return stringRedisTemplate;
    }
}
