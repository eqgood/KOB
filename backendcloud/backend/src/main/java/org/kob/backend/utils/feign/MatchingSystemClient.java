package org.kob.backend.utils.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "matchingsystem", fallbackFactory = MatchingSystemClientFallbackFactory.class)
public interface MatchingSystemClient {

    @PostMapping("/player/add/")
    String addPlayer(@RequestParam("user_id") Integer userId,
                     @RequestParam("rating") Integer rating,
                     @RequestParam("bot_id") Integer botId);

    @PostMapping("/player/remove/")
    String removePlayer(@RequestParam("user_id") Integer userId);
}
