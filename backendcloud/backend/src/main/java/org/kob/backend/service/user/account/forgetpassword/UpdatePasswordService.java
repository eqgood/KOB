package org.kob.backend.service.user.account.forgetpassword;

import java.util.Map;

public interface UpdatePasswordService {
    Map<String, String> updatePassword(String email, String verifyCode, String password, String confirmedPassword);
}
