package org.kob.backend.service.impl.user.account.forgetpassword;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.kob.backend.mapper.UserMapper;
import org.kob.backend.pojo.User;
import org.kob.backend.service.user.account.forgetpassword.UpdatePasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static org.kob.backend.consumer.WebSocketServer.userMapper;

@Service
public class UpdatePasswordServiceImpl implements UpdatePasswordService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserMapper userMapper;

    @Override
    public Map<String, String> updatePassword(String email, String verifyCode, String password, String confirmedPassword) {
        Map<String, String> map = new HashMap<>();

        if(verifyCode == null){
            map.put("message", "验证码不能为空");
            return map;
        }
        if(password == null || confirmedPassword == null){
            map.put("message", "密码不能为空");
            return map;
        }
        if (!StringUtils.hasText(email)) {
            map.put("message", "邮箱不能为空");
            return map;
        }

        if(email.length() > 1000){
            map.put("message", "邮箱长度不能超过1000");
            return map;
        }

        String emailRegex = "^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.matches(emailRegex)) {
            map.put("message", "邮箱格式不正确");
            return map;
        }

        String redisKey = "verify_code:" + email;
        String redisVerifyCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (redisVerifyCode == null || !redisVerifyCode.equals(verifyCode)) {
            map.put("message", "验证码错误或者已经过期");
            return map;
        }

        if(password.length() > 100 || confirmedPassword.length() > 100){
            map.put("message","密码长度不能大于100");
            return map;
        }

        if(!password.equals(confirmedPassword)){
            map.put("message","两次输入的密码不一致");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = userMapper.selectOne(queryWrapper);

        if(user == null){
            map.put("message", "邮箱对应的角色不存在");
            return map;
        }

        stringRedisTemplate.delete(redisKey);

        String encodedPassword = passwordEncoder.encode(password);
        User updateUser =  new User();
        updateUser.setId(user.getId());
        updateUser.setPassword(encodedPassword);
        userMapper.updateById(updateUser);
        map.put("message", "success");
        return map;
    }
}
