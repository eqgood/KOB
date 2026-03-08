package org.kob.backend.service.user.account;

import java.util.Map;

public interface SendVerifyCodeService {
    Map<String, String> sendVerifyCode(String email, String scene);
}
