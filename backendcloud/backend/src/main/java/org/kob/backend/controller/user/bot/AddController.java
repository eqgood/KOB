package org.kob.backend.controller.user.bot;

import org.kob.backend.service.user.account.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AddController {
    @Autowired
    private AddService addService;

    @PostMapping("/api/user/bot/add/")
    public Map<String, String> add(@RequestParam Map<String, String> data){
        return addService.add(data);
    }
}
