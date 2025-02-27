package com.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_group_buying_member")
public class GroupBuyingMember {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer groupBuyingId;
    private String userId;
    private Date joinTime;

    @TableField(exist = false)
    private WxUserInfo wxUserInfo;
}