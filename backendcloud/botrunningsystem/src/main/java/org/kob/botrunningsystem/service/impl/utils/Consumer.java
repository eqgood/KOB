package org.kob.botrunningsystem.service.impl.utils;

import org.kob.botrunningsystem.utils.BotTemplate;
import org.kob.botrunningsystem.utils.sandbox.BotSecureSandbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.UUID;

@Component
public class Consumer {
    private static RestTemplate restTemplate;
    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Consumer.restTemplate = restTemplate;
    }


    private String addUid(String code, String uid) {
        int k = code.indexOf(" implements java.util.function.Supplier<Integer>");
        return code.substring(0, k) + uid + code.substring(k);
    }

    // 调用沙箱
    public void executeBot(Bot bot) {

        // 写 input.txt
        File file = new File("input.txt");
        try (PrintWriter fout = new PrintWriter(file)) {
            fout.println(bot.getInput());
            fout.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        String finalBotCode = BotTemplate.fill(bot.getBotCode());
        String fullClassName = "org.kob.botrunningsystem.utils.Bot"; // 模板里固定的类名
        // ================================================================

        // ===================== 保留：沙箱执行 =====================


        try {
            Integer direction = BotSecureSandbox.executeSafe(finalBotCode, fullClassName);
            MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
            data.add("user_id", bot.getUserId().toString());
            data.add("direction", direction.toString());
            restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
        } catch (SecurityException e) {
            System.err.println("沙箱拦截：恶意代码 -> " + bot.getUserId() + "，原因：" + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bot 执行失败：" + bot.getUserId());
            e.printStackTrace();
        }
    }
}