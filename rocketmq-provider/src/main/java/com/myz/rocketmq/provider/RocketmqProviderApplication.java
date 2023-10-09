package com.myz.rocketmq.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.myz.rocketmq.provider.mapper")
public class RocketmqProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(RocketmqProviderApplication.class, args);
    }
}
