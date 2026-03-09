package org.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.kob.backend.mapper.UserMapper;
import org.kob.backend.pojo.User;
import org.kob.backend.service.impl.utils.VerifyCodeUtil;
import org.kob.backend.service.user.account.SendVerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SendVerifyCodeServiceImpl implements SendVerifyCodeService {
    @Autowired
    private UserMapper userMapper;

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String fromEmail;
    @Override
    public Map<String, String> sendVerifyCode(String email, String scene) {
        Map<String, String> map = new HashMap<>();
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
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null && "forget_password".equals(scene)){
                map.put("message", "邮箱未注册");
                return map;
        }else if(user != null && "register".equals(scene)){
            map.put("message", "邮箱已注册");
            return map;
        }

        String verifyCode = VerifyCodeUtil.generate6DigitCode();

        String redisKey = "verify_code:" + email;
        stringRedisTemplate.delete(redisKey);
        stringRedisTemplate.opsForValue().set(redisKey, verifyCode, 300, TimeUnit.SECONDS);

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail); // 发件人
            mailMessage.setTo(email); // 收件人
            if("forget_password".equals(scene)){
                mailMessage.setSubject("密码重置验证码"); // 邮件标题
                mailMessage.setText("您好，您的密码重置验证码是：" + verifyCode +
                        "\n验证码有效期5分钟，请及时使用，请勿泄露给他人！");
            }else if("register".equals(scene)) {
                mailMessage.setSubject("注册验证码"); // 邮件标题
                mailMessage.setText("您好，您的注册验证码是：" + verifyCode +
                        "\n验证码有效期5分钟，请及时使用，请勿泄露给他人！");
            }

            // 发送邮件
            javaMailSender.send(mailMessage);

            // 发送成功
            map.put("message", "success");
        } catch (Exception e) {

            map.put("message", "验证码发送失败，请稍后重试");
        }

        return map;
    }
}
