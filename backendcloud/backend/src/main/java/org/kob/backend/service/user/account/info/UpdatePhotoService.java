package org.kob.backend.service.user.account.info;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UpdatePhotoService {
    Map<String, String> updatePhoto(MultipartFile file);
}
