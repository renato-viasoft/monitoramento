package com.viasoft.ratelimiter.service;

import com.viasoft.ratelimiter.model.ApiAccessLimit;

import java.util.List;

public interface ApiAccessLimitService {
    List<ApiAccessLimit> listAccessLimitsByApiKey(String apiKey);

    void cleanAccessCashByApiKey(String apiKey);
}
