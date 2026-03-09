package org.kob.backend.service.user.account.info;

import java.util.Map;

public interface UpdateInfoService {
    Map<String, String> updateInfo(String username, String description, String email);
}
