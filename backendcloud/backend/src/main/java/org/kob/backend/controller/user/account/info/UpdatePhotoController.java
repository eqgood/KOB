package org.kob.backend.controller.user.account.info;

import org.kob.backend.service.user.account.info.UpdatePhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class UpdatePhotoController {
    @Autowired
    private UpdatePhotoService updatePhotoService;

    @PostMapping("/api/user/account/updatephoto/")
    public Map<String, String> updatePhoto(@RequestParam("photo") MultipartFile file){
        return updatePhotoService.updatePhoto(file);
    }
}
