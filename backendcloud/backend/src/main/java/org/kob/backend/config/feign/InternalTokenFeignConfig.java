package org.kob.backend.config.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalTokenFeignConfig {

    @Value("${internal.token}")
    private String internalToken;

    @Bean
    public RequestInterceptor internalTokenRequestInterceptor() {
        return template -> template.header("X-Internal-Token", internalToken);
    }
}
