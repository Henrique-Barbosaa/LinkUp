package com.linkup.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.linkup.repository.UrlRepository;

import jakarta.transaction.Transactional;

import java.util.Set;

@Component
public class ClickSyncTask {

    private final StringRedisTemplate redisTemplate;
    private final UrlRepository repository;

    public ClickSyncTask(StringRedisTemplate redisTemplate, UrlRepository repository) {
        this.redisTemplate = redisTemplate;
        this.repository = repository;
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void syncClicksToDatabase() {
        Set<String> keys = redisTemplate.keys("clicks:*");

        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                String shortCode = key.replace("clicks:", "");
                
                String value = redisTemplate.opsForValue().get(key);
                
                if (value != null) {
                    Long clicksInCache = Long.valueOf(value);

                    repository.updateClickCount(shortCode, clicksInCache);

                    redisTemplate.delete(key);
                }
            }
        }
    }
}