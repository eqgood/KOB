package org.kob.backend.utils.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "botrunningsystem")
public interface BotRunningSystemClient {

    @PostMapping("/bot/add/")
    String addBot(@RequestParam("user_id") Integer userId,
                  @RequestParam("bot_code") String botCode,
                  @RequestParam("input") String input);
}
