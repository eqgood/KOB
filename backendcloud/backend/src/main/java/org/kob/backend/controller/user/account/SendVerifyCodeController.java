package org.kob.backend.controller.user.account;

import org.kob.backend.service.user.account.SendVerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SendVerifyCodeController {

    @Autowired
    private SendVerifyCodeService sendVerifyCodeService;
    @PostMapping("/api/user/account/send_verifycode/")
    public Map<String, String> sendVerifyCode(@RequestParam String email, String scene) {
        return sendVerifyCodeService.sendVerifyCode(email, scene);
    }
}
