package com.viasoft.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
public class AuthorizationFilterFactory implements GatewayFilterFactory<AuthorizationFilterFactory.Config> {

    private final WebClient webClient;
    private final String authServiceUrl;

    public AuthorizationFilterFactory(
        WebClient.Builder webClientBuilder,
        @Value("${LIMITER_URI:http://localhost:8080}") String authServiceUrl
    ) {
        this.webClient = webClientBuilder.build();
        this.authServiceUrl = authServiceUrl;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new AuthorizationFilter(webClient, authServiceUrl);
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    public static class Config {}
}

class AuthorizationFilter implements GatewayFilter {
    private final WebClient webClient;
    private final String authServiceUrl;

    public AuthorizationFilter(
        WebClient webClientBuilder,
        String authServiceUrl
    ) {
        this.webClient = webClientBuilder;
        this.authServiceUrl = authServiceUrl;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String key = exchange.getRequest().getHeaders().getFirst("api_key");

        return webClient.get()
            .uri(authServiceUrl + "/rate-limiter/" + key)
            .retrieve()
            .onStatus((HttpStatusCode::is4xxClientError), CustomErrorHandler::handleError)
            .toBodilessEntity()
            .flatMap(res ->  chain.filter(exchange));
    }
}