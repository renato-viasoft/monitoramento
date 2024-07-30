package com.viasoft.ratelimiter.service;

import com.viasoft.ratelimiter.model.ApiAccessLimit;
import com.viasoft.ratelimiter.model.RefillTypeEnum;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConfigurationBuilder;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.serialization.Mapper;
import io.github.bucket4j.redis.jedis.Bucket4jJedis;
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import java.time.Duration;
import java.util.List;

@Service
public class Bucket4jService {
    private final JedisPool jedisPool;
    private final ApiAccessLimitService apiAccessLimitService;

    public Bucket4jService(JedisPool jedisPool, ApiAccessLimitService apiAccessLimitService) {
        this.jedisPool = jedisPool;
        this.apiAccessLimitService = apiAccessLimitService;
    }

    private JedisBasedProxyManager<String> getProxyManager() {
        return Bucket4jJedis.casBasedBuilder(jedisPool)
                .expirationAfterWrite(
                        ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofSeconds(10))
                ).keyMapper(Mapper.STRING)
                .build();
    }

    public Bucket getBucket(String key) {
        ConfigurationBuilder builder = BucketConfiguration.builder();
        List<ApiAccessLimit> limits = apiAccessLimitService.listAccessLimitsByApiKey(key);

        int defaultCapacity = 100;
        builder.addLimit((limit ->
            limit.capacity(defaultCapacity).refillIntervally(defaultCapacity, Duration.ofSeconds(1)).id("default")
        ));

        limits.forEach((accessLimit) -> {
            builder.addLimit((limit ->
                accessLimit.getRefillType().equals(RefillTypeEnum.INTERVAL) ?
                    limit.capacity(accessLimit.getCapacity()).refillIntervally(accessLimit.getCapacity(), Duration.ofSeconds(accessLimit.getRefillIntervalSeconds())).id(accessLimit.getId().toString()) :
                    limit.capacity(accessLimit.getCapacity()).refillGreedy(accessLimit.getCapacity(), Duration.ofSeconds(accessLimit.getRefillIntervalSeconds())).id(accessLimit.getId().toString())
            ));
        });

        return getProxyManager().getProxy(key, builder::build);
    }
}