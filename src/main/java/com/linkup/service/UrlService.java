package com.linkup.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linkup.dto.UrlStatsResponse;
import com.linkup.model.UrlMapping;
import com.linkup.repository.UrlRepository;
import com.linkup.util.Base62Converter;

import java.time.Duration;

@Service
public class UrlService {

    private final UrlRepository repository;
    private final Base62Converter converter;
    private final StringRedisTemplate redisTemplate;

    public UrlService(UrlRepository repository, Base62Converter converter, StringRedisTemplate redisTemplate) {
        this.repository = repository;
        this.converter = converter;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public String shortenUrl(String originalUrl) {
        UrlMapping entity = new UrlMapping();
        entity.setOriginalUrl(originalUrl);
        entity = repository.save(entity);

        String shortCode = converter.encode(entity.getId());

        entity.setShortCode(shortCode);
        repository.save(entity);

        redisTemplate.opsForValue().set(shortCode, originalUrl, Duration.ofHours(24));

        return shortCode;
    }

    public String getOriginalUrl(String shortCode) {
        String cachedUrl = redisTemplate.opsForValue().get(shortCode);
        if (cachedUrl != null) {
            redisTemplate.opsForValue().increment("clicks:" + shortCode);
            return cachedUrl;
        }

        UrlMapping entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL não encontrada para o código: " + shortCode));

        redisTemplate.opsForValue().set(shortCode, entity.getOriginalUrl(), Duration.ofHours(24));
        redisTemplate.opsForValue().increment("clicks:" + shortCode);

        return entity.getOriginalUrl();
    }

    public UrlStatsResponse getStats(String shortCode) {
        UrlMapping entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL não encontrada para o código: " + shortCode));

        String redisClicks = redisTemplate.opsForValue().get("clicks:" + shortCode);
        long clicksInRedis = (redisClicks != null) ? Long.parseLong(redisClicks) : 0L;

        return new UrlStatsResponse(
                entity.getOriginalUrl(),
                entity.getClickCount() + clicksInRedis,
                entity.getCreatedAt()
        );
    }
}