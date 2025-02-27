package com.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@TableName("t_group_buying")
public class GroupBuying implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer productId;

    private String userId;  // 添加用户ID字段

    private Date startTime; // 添加开始时间字段

    private Integer status; // 0:进行中 1:已完成 2:已取消

    // 添加 joinedNumber 属性
    private int joinedNumber;//当前人数

    // 添加 requiredNumber 属性
    private int requiredNumber;//最低人数

    // 添加 endTime 属性
    private Date endTime;

    // 添加 groupPrice 属性
    private BigDecimal groupPrice;

    @TableField(exist = false)
    private Product product;

    @TableField(exist = false)
    private List<GroupBuyingMember> members;
}