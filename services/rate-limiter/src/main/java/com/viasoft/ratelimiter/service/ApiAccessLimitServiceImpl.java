package com.viasoft.ratelimiter.service;

import com.viasoft.ratelimiter.data.ApiAccessLimitData;
import com.viasoft.ratelimiter.model.ApiAccessLimit;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ApiAccessLimitServiceImpl implements ApiAccessLimitService {
    private static final HashMap<String, List<ApiAccessLimit>> accessesCashed = new HashMap<>();
    private final ApiAccessLimitData apiAccessLimitData;

    public ApiAccessLimitServiceImpl(ApiAccessLimitData apiAccessLimitData) {
        this.apiAccessLimitData = apiAccessLimitData;
    }

    @Override
    public List<ApiAccessLimit> listAccessLimitsByApiKey(String apiKey) {
        boolean hasAccesses = accessesCashed.containsKey(apiKey);
        System.out.println(hasAccesses);
        if (hasAccesses) {
            return accessesCashed.get(apiKey);
        }

        List<ApiAccessLimit> accesses = apiAccessLimitData.findByApiKey(apiKey);
        accessesCashed.put(apiKey, accesses);

        return accesses;
    }

    @Override
    public void cleanAccessCashByApiKey(String apiKey) {
        accessesCashed.remove(apiKey);
    }
}
