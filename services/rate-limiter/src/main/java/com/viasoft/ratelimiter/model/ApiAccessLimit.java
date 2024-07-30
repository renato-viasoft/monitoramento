package com.viasoft.ratelimiter.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "api_access_limit")
@Data
public class ApiAccessLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "refil_type")
    @Enumerated(EnumType.STRING)
    RefillTypeEnum refillType;

    @Column(name = "capacity")
    private Long capacity;

    @Column(name = "refill_interval_seconds")
    private Long refillIntervalSeconds;
}

