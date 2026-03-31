package org.kob.backend.utils.feign;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class MatchingSystemClientFallbackFactory implements FallbackFactory<MatchingSystemClient> {
    @Override
    public MatchingSystemClient create(Throwable cause) {
        return new MatchingSystemClient() {
            @Override
            public String addPlayer(Integer userId, Integer rating, Integer botId) {
                return "matching service unavailable";
            }

            @Override
            public String removePlayer(Integer userId) {
                return "matching service unavailable";
            }
        };
    }
}
