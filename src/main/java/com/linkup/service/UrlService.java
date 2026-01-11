package com.linkup.service;

import org.hashids.Hashids;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.linkup.advice.exceptions.InvalidUrlException;
import com.linkup.advice.exceptions.UrlNotFoundException;
import com.linkup.dto.UrlStatsResponse;
import com.linkup.model.UrlMapping;
import com.linkup.repository.UrlRepository;

import java.time.Duration;

@Service
public class UrlService {

    private final UrlRepository repository;
    private final Hashids hashids;
    private final StringRedisTemplate redisTemplate;
    private final WebClient webClient;

    public UrlService(
        UrlRepository repository, 
        Hashids hashids, 
        StringRedisTemplate redisTemplate, 
        WebClient webClient
    ) {
        this.repository = repository;
        this.hashids = hashids;
        this.redisTemplate = redisTemplate;
        this.webClient = webClient;
    }

    private boolean isUrlAlive(String url) {
        try {
            return Boolean.TRUE.equals(
                webClient.head()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> response.getStatusCode().is2xxSuccessful())
                    .onErrorReturn(false)
                    .block()
            );
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public String shortenUrl(String originalUrl) {
        if (!isUrlAlive(originalUrl)) {
            throw new InvalidUrlException("A URL fornecida está inacessível ou é inválida.");
        }

        UrlMapping entity = new UrlMapping();
        entity.setOriginalUrl(originalUrl);
        entity = repository.save(entity);

        String shortCode = hashids.encode(entity.getId());

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
                .orElseThrow(() -> new UrlNotFoundException("URL não encontrada para o código: " + shortCode));

        redisTemplate.opsForValue().set(shortCode, entity.getOriginalUrl(), Duration.ofHours(24));
        redisTemplate.opsForValue().increment("clicks:" + shortCode);

        return entity.getOriginalUrl();
    }

    public UrlStatsResponse getStats(String shortCode) {
        UrlMapping entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL não encontrada para o código: " + shortCode));

        String redisClicks = redisTemplate.opsForValue().get("clicks:" + shortCode);
        long clicksInRedis = (redisClicks != null) ? Long.parseLong(redisClicks) : 0L;

        return new UrlStatsResponse(
                entity.getOriginalUrl(),
                entity.getClickCount() + clicksInRedis,
                entity.getCreatedAt()
        );
    }
}