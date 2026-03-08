package org.kob.backend.service.impl.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class VerifyCodeUtil {
    // 生成6位数字验证码
    public static String generate6DigitCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
