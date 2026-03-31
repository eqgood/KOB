package org.kob.backend.config.feign;


import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignRetryConfig {

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(100, 500, 2);
    }
}