package com.linkup.config;

import org.hashids.Hashids;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HashConfig {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String SALT = "3d62b621-56cb-45cd-b38f-42843888b1a9";

    @Bean
    public Hashids hashids() {
        return new Hashids(SALT, 4, CHARACTERS);
    }
}