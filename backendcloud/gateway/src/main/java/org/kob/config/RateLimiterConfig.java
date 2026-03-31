package org.kob.config;

import io.jsonwebtoken.Claims;
import org.kob.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.List;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            String userId = getUserIdFromToken(request);
            if (userId != null) {
                return Mono.just("user:" + userId);
            }
            return Mono.just("ip:" + getClientIp(request));
        };
    }

    private String getUserIdFromToken(ServerHttpRequest request) {
        String auth = request.getHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            try {
                Claims claims = JwtUtil.parseJWT(auth.substring(7));
                return claims.getSubject();
            } catch (Exception ignored) {}
        }
        return null;
    }

    private String getClientIp(ServerHttpRequest request) {
        List<String> xff = request.getHeaders().get("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            String ip = xff.get(0).split(",")[0].trim();
            if (!"unknown".equalsIgnoreCase(ip)) return ip;
        }
        InetSocketAddress addr = request.getRemoteAddress();
        return addr != null ? addr.getAddress().getHostAddress() : "unknown";
    }
}