package com.maksim.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class AuthFilter implements GlobalFilter, Ordered {
    private final WebClient authServiceWebClient;

    public AuthFilter(@Value("${auth.service.url}") String url) {
        this.authServiceWebClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove("X-User-Id");
                    headers.remove("X-User-Handle");
                })
                .build();

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        return validateToken(token)
                .flatMap(response -> {
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .header("X-User-Id", response.id().toString())
                            .header("X-User-Handle", response.handle())
                            .build();
                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(mutatedRequest)
                            .build();
                    return chain.filter(mutatedExchange);
                })
                .onErrorResume(e -> onError(exchange));
    }

    private Mono<ValidateResponse> validateToken(String token) {
        ValidateRequest request = new ValidateRequest(token);
        return authServiceWebClient
                .post()
                .uri("/api/auth/validate")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        {
                            return Mono.error(new RuntimeException());
                        }
                )
                .bodyToMono(ValidateResponse.class)
                .timeout(java.time.Duration.ofSeconds(3));
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = response.bufferFactory()
                .wrap(("{\"message\":\"Invalid token\"}").getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 10000;
    }
}