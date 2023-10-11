package com.myz.rocketmq.provider.controller;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final StringRedisTemplate redisTemplate;
    private final RocketMQTemplate rocketMQTemplate;

    AtomicInteger userIdAt = new AtomicInteger(0);

    public TaskController(StringRedisTemplate redisTemplate, RocketMQTemplate rocketMQTemplate) {

        this.redisTemplate = redisTemplate;
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @GetMapping("/doSecKill")
    public Object doSecKill(Integer goodsId) {
        int userId = userIdAt.incrementAndGet();
        Long count = redisTemplate.opsForValue().decrement("goodsId:" + goodsId);
        if (count < 0) {
            return "该商品已抢完，下次早点来";
        }
        String uk = userId + "-" + goodsId;
        Boolean flag = redisTemplate.opsForValue().setIfAbsent("uk:"+uk, "");
        if (!flag) {

            return "你已经参与过该商品的抢购，请参与其他商品";
        }
        //rocketmq异步处理
        rocketMQTemplate.asyncSend("secKillTopic", uk, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("发送失败:"+throwable.getMessage());
            }
        }, 500L);
        return "抢购中······";
    }
}
