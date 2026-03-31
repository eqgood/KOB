package org.kob.backend.utils.feign;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class BotRunningSystemClientFallbackFactory implements FallbackFactory<BotRunningSystemClient> {
    @Override
    public BotRunningSystemClient create(Throwable cause) {
        return (userId, botCode, input) -> "botrruning service unavailable";
    }
}
