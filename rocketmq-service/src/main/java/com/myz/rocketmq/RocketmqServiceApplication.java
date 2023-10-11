package com.myz.rocketmq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hello world!
 */
@SpringBootApplication
@MapperScan(basePackages = "com.myz.rocketmq.mapper")
public class RocketmqServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RocketmqServiceApplication.class, args);
    }
}
