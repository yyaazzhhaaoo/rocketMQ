package com.myz.rocketmq.mapper;

import com.myz.rocketmq.bean.Order;

/**
* @author meiyazhao
* @description 针对表【order】的数据库操作Mapper
* @createDate 2023-10-10 16:27:49
* @Entity com.myz.rocketmq.bean.Order
*/
public interface OrderMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

}
