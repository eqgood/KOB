package org.kob.backend.controller.user.account.info;

import org.kob.backend.service.user.account.info.UpdateInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UpdateInfoController {
    @Autowired
    private UpdateInfoService updateInfoService;

    @PostMapping("/api/user/account/updateinfo/")
    public Map<String, String> updateInfo(@RequestParam Map<String, String> data) {
        String username = data.get("username");
        String description = data.get("description");
        String email = data.get("email");
        return updateInfoService.updateInfo(username, description, email);
    }
}
