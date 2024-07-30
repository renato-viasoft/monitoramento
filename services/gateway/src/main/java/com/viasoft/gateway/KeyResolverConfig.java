package com.viasoft.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class KeyResolverConfig {
    private static final Logger logger = LoggerFactory.getLogger(KeyResolverConfig.class);
    @Bean
    public KeyResolver keyResolver() {
        String headerName = "key";
        return exchange -> {
            String key = exchange.getRequest().getHeaders().getFirst(headerName);
            logger.info("KeyResolver resolved key: {}", key);
            return Mono.justOrEmpty(key);
        };
    }
}
