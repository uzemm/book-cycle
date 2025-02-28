package com.uzem.book_cycle.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisTemplate<String, String> redisBlackListTemplate;

    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // accessToken 블랙리스트 추가
    public void setBlackList(String token, String type, Long expiration) {
        redisBlackListTemplate.opsForValue().set(token, type, expiration, TimeUnit.MILLISECONDS);
    }

    // key 존재 여부 확인
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
