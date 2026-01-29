package org.kob.backend.service.user.account;

import java.util.Map;

public interface LoginService {
    Map<String, String> GetToken(String username, String password);
}
