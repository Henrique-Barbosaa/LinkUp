package com.linkup.config;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HashConfig {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Value("${app.hashids.salt}")
    private String salt;

    @Bean
    public Hashids hashids() {
        return new Hashids(salt, 4, CHARACTERS);
    }
}