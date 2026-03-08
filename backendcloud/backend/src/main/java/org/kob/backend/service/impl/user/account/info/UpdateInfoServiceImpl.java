package org.kob.backend.service.impl.user.account.info;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.kob.backend.mapper.UserMapper;
import org.kob.backend.pojo.User;
import org.kob.backend.service.impl.utils.UserDetailsImpl;
import org.kob.backend.service.user.account.info.UpdateInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UpdateInfoServiceImpl  implements UpdateInfoService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public Map<String, String> updateInfo(String username, String description) {
        System.out.println("update info: " + username + " " + description);
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
            User user = loginUser.getUser();
        Map<String, String> map = new HashMap<>();

        if(username == null){
            map.put("message","用户名不能为空");
            return map;
        }

        username = username.trim();

        if(username.isEmpty()){
            map.put("message","用户名不能为空");
            return map;
        }

        if(username.length()>100){
            map.put("message","用户名长度不能大于100");
            return map;
        }

        if(description.length() > 1000){
            map.put("message","简介长度不能超过1000");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> users = new ArrayList<>();
        users = userMapper.selectList(queryWrapper);
        if(!users.isEmpty()){
            if(!user.getUsername().equals(users.get(0).getUsername())) {
                map.put("message", "用户名已存在");
                return map;
            }
        }
        User newUser = new User(
                user.getId(),
                username,
                user.getPassword(),
                user.getPhoto(),
                user.getRating(),
                user.getOpenid(),
                description,
                user.getEmail()
        );
        userMapper.updateById(newUser);
        map.put("message", "success");
        return map;
    }
}
