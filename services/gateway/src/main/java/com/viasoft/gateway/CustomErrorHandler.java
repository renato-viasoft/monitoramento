package com.viasoft.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public class CustomErrorHandler {
    public static Mono<ResponseStatusException> handleError(ClientResponse response) {
        var exception = new ResponseStatusException(response.statusCode());

        if (response.statusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            StackTraceElement[] arr = {};
            exception.setStackTrace(arr);
        }

        return Mono.error(exception);
    }
}