package com.myz.rocketmq.bean;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName order
 */
@Data
public class Order implements Serializable {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private Integer userId;

    /**
     * 
     */
    private Integer goodsId;

    /**
     * 
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}