package com.myz.rocketmq.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName goods
 */
@Data
public class Goods implements Serializable {
    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 现价
     */
    private BigDecimal price;

    /**
     * 详细描述
     */
    private String content;

    /**
     * 
     */
    private Integer status;

    /**
     * 总库存
     */
    private Integer totalStocks;

    /**
     * 录入时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否参于秒杀1是0否
     */
    private Integer spike;

    private static final long serialVersionUID = 1L;
}