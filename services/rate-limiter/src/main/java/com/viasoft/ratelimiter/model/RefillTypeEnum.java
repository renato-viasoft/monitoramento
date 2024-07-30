package com.viasoft.ratelimiter.model;

public enum RefillTypeEnum {
    GREEDY("GREEDY"),
    INTERVAL("INTERVAL");

    private final String value;

    RefillTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

