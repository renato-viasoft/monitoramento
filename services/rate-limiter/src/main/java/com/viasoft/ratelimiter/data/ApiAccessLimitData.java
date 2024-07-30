package com.viasoft.ratelimiter.data;

import com.viasoft.ratelimiter.model.ApiAccessLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiAccessLimitData extends JpaRepository<ApiAccessLimit, Long> {
    List<ApiAccessLimit> findByApiKey(String apiKey);
}
