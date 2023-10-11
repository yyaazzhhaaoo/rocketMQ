package com.myz.rocketmq.listener;

import com.myz.rocketmq.service.GoodsService;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RocketMQMessageListener(topic = "secKillTopic",
        consumerGroup = "task-consumer-group",
        consumeMode = ConsumeMode.CONCURRENTLY,
        consumeThreadNumber = 16)
public class SecKillListener implements RocketMQListener<MessageExt> {

    private final GoodsService goodsService;
    private final StringRedisTemplate redisTemplate;

    public SecKillListener(GoodsService goodsService, StringRedisTemplate redisTemplate) {
        this.goodsService = goodsService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(MessageExt messageExt) {
        String msg = new String(messageExt.getBody());
        int userId = Integer.parseInt(msg.split("-")[0]);
        int goodsId = Integer.parseInt(msg.split("-")[1]);
        //增加redis分布式锁
        String lockKey = String.valueOf(goodsId);
        String requestId = UUID.randomUUID().toString();
        while (true){
            Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, Duration.ofSeconds(5));
            if (Boolean.TRUE.equals(result)){
                try {
                    goodsService.realSecKill(userId,goodsId);
                    return;
                }finally {
                    redisTemplate.delete(lockKey);
                }
            }
        }
    }
}
