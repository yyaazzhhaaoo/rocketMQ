package com.myz.rocketmq.consumer.config;

import com.myz.rocketmq.service.RocketTemplate;
import com.myz.rocketmq.service.impl.RocketTemplateImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigBeanTemplate {

    @Bean
    public RocketTemplate rocketTemplate(){
        return new RocketTemplateImpl();
    }
}
