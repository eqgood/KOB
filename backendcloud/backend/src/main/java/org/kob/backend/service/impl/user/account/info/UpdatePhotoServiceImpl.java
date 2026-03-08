package org.kob.backend.service.impl.user.account.info;

import org.kob.backend.mapper.UserMapper;
import org.kob.backend.pojo.User;
import org.kob.backend.service.impl.utils.UserDetailsImpl;
import org.kob.backend.service.user.account.info.UpdatePhotoService;
import org.kob.backend.utils.OSSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class UpdatePhotoServiceImpl implements UpdatePhotoService {
    private OSSUtil ossUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private void SetOSSUtil(OSSUtil ossUtil) {
        this.ossUtil = ossUtil;
    }



    @Override
    public Map<String, String> updatePhoto(MultipartFile file) {
        Map<String, String> map = new HashMap<>();
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        Integer userId = loginUser.getUser().getId();

        try {
            String avatarUrl = ossUtil.uploadAvatar(file);
            User user = new User();
            user.setId(userId);
            user.setPhoto(avatarUrl);
            userMapper.updateById(user);
            map.put("message", "success");
            map.put("photo", avatarUrl);
            return map;
        } catch (Exception e) {
            map.put("message", "更新失败");
            return map;
        }
    }
}
