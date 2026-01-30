package org.kob.matchingsystem;

import org.kob.matchingsystem.Service.impl.MatchingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MatchingSystemApplication {
    public static void main(String[] args) {
        MatchingServiceImpl.matchingPool.start(); // 启动匹配线程
        ConfigurableApplicationContext context = SpringApplication.run(MatchingSystemApplication.class, args);

    }
}
