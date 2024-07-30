package com.viasoft.ratelimiter;

import com.viasoft.ratelimiter.service.ApiAccessLimitService;
import com.viasoft.ratelimiter.service.Bucket4jService;
import io.github.bucket4j.Bucket;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RateLimiterController {
    private final Bucket4jService bucket4jService;
    private final ApiAccessLimitService apiAccessLimitService;

    public RateLimiterController(Bucket4jService bucket4jService, ApiAccessLimitService apiAccessLimitService) {
        this.bucket4jService = bucket4jService;
        this.apiAccessLimitService = apiAccessLimitService;
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok("Pong");
    }

    @GetMapping("/rate-limiter/{apiKey}")
    public ResponseEntity<?> rateLimiter(
        @PathVariable String apiKey
    ) {
        Bucket bucket = bucket4jService.getBucket(apiKey);
        if (!bucket.tryConsume(1)) {
            System.out.println("Rate limit exceeded for key: " + apiKey);
            return ResponseEntity.status(429).build();
        }

        return  ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rate-limiter/{apiKey}")
    public ResponseEntity<?> removeCash(
            @PathVariable String apiKey
    ) {
        apiAccessLimitService.cleanAccessCashByApiKey(apiKey);
        bucket4jService.getBucket(apiKey).reset();
        return  ResponseEntity.noContent().build();
    }
}
