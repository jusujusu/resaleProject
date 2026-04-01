package com.example.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * FileName    : RedisService
 * Since       : 26. 4. 1.
 * Dsecription  : JWT의 RefreshToken을 Redis에 저장하고 관리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    /*
     * 토큰 저장 (Key: 이메일 / Value: RefreshToken)
     * */
    public void setValues(
            String key,
            String value,
            Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    /*
     * 토큰 조회
     * */
    public String getValues(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /*
    * 토큰 삭제 (로그 아웃시)
    * */
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

}
