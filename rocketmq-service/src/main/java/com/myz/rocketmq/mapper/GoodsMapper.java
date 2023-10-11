package com.myz.rocketmq.mapper;

import com.myz.rocketmq.bean.Goods;

import java.util.List;

/**
* @author meiyazhao
* @description 针对表【goods】的数据库操作Mapper
* @createDate 2023-10-10 16:27:49
* @Entity com.myz.rocketmq.bean.Goods
*/
public interface GoodsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(int id);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKey(Goods record);

    List<Goods> list();
}
