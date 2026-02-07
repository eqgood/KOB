package org.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.kob.backend.mapper.UserMapper;
import org.kob.backend.pojo.User;
import org.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String, String> map = new HashMap<>();
        if(username == null){
            map.put("message","用户名不能为空");
            return map;
        }

        if (password == null) {
            map.put("message", "密码不能为空");
            return map;
        }

        username = username.trim();

        if(username.isEmpty()){
            map.put("message","用户名不能为空");
            return map;
        }

        if(password.isEmpty() && confirmedPassword.isEmpty()){
            map.put("message","密码不能为空");
            return map;
        }

        if(username.length()>100){
            map.put("message","用户名长度不能大于100");
            return map;
        }

        if(password.length() > 100 && confirmedPassword.length() > 100){
            map.put("message","密码长度不能大于100");
            return map;
        }

        if(!password.equals(confirmedPassword)){
            map.put("message","两次输入的密码不一致");
            return map;
        }


        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("username",username);
        List<User> users = userMapper.selectList(queryWrapper);
        if(!users.isEmpty()){
            map.put("message", "用户名已存在");
            return map;
        }

        // 插入用户信息，密码需要加密存储
        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/163117_lg_3ed5b29c93.jpg";
        User user = new User(null, username, encodedPassword ,photo, 1500,null);
        userMapper.insert(user);
        map.put("message","success");
        return map;
    }
}
