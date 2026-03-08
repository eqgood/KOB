package org.kob.backend.controller.user.account.forgetpassword;

import org.kob.backend.service.user.account.forgetpassword.UpdatePasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UpdatePasswordController {

    @Autowired
    private UpdatePasswordService updatePasswordService;

    @PostMapping("/api/user/account/forget_password/update_password/")
    public Map<String, String> updatePassword(String email, String verifyCode, String Password, String confirmedPassword) {
        return updatePasswordService.updatePassword(email, verifyCode, Password, confirmedPassword);
    }
}
