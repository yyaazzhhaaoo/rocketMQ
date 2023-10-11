package com.myz.rocketmq.service.impl;

import com.myz.rocketmq.bean.Goods;
import com.myz.rocketmq.bean.Order;
import com.myz.rocketmq.mapper.GoodsMapper;
import com.myz.rocketmq.mapper.OrderMapper;
import com.myz.rocketmq.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class GoodsServiceImpl implements GoodsService {
    private final GoodsMapper goodsMapper;
    private final OrderMapper orderMapper;
    public GoodsServiceImpl(GoodsMapper goodsMapper, OrderMapper orderMapper) {
        this.goodsMapper = goodsMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void realSecKill(int userId, int goodsId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        int i = goods.getTotalStocks() - 1;
        if (i<0){
            throw new RuntimeException("商品"+goodsId+"库存不足,用户id为"+userId);
        }
        goods.setTotalStocks(i);
        goods.setUpdateTime(new Date());
        int i1 = goodsMapper.updateByPrimaryKey(goods);
        if (i1>0){
            Order order = new Order();
            order.setGoodsId(goodsId);
            order.setUserId(userId);
            order.setCreateTime(new Date());
            orderMapper.insert(order);
        }
    }
}
