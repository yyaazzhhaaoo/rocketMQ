package com.myz.rocketmq.config;

import com.myz.rocketmq.bean.Goods;
import com.myz.rocketmq.mapper.GoodsMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class DataSync {

    private final GoodsMapper goodsMapper;
    private final StringRedisTemplate redisTemplate;

    public DataSync(GoodsMapper goodsMapper, StringRedisTemplate redisTemplate){
        this.goodsMapper = goodsMapper;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void initData(){
        List<Goods> list = goodsMapper.list();
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        for (Goods goods : list) {
            redisTemplate.opsForValue().set("goodsId:"+goods.getGoodsId(),goods.getTotalStocks().toString());
        }
    }

}
