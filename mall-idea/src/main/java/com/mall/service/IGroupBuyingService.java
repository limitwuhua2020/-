package com.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.entity.GroupBuying;

import java.util.List;
import java.util.Map;

public interface IGroupBuyingService extends IService<GroupBuying> {
    /**
     * 创建团购活动
     */
    GroupBuying createGroup(GroupBuying groupBuying);

    /**
     * 获取商品的活动团购信息
     * @param productId 商品ID
     * @return 团购信息
     */
    GroupBuying getActiveGroupByProductId(Integer productId);

    /**
     * 加入团购
     * @param groupId 团购ID
     * @param userId 用户ID
     * @return 加入结果
     */
    Map<String, Object> joinGroup(Integer groupId, String userId);

    /**
     * 获取团购详情
     * @param groupId 团购ID
     * @return 团购详情
     */
    GroupBuying getGroupDetail(Integer groupId);

    /**
     * 获取进行中的团购
     */
    List<GroupBuying> getActiveGroups();

    /**
     * 检查团购状态
     */
    void checkGroupStatus(Integer groupId);

    /**
     * 创建团购订单
     * @param groupId 团购ID
     * @param userId 用户ID
     * @return 订单信息
     */
    Map<String, Object> createGroupOrder(Integer groupId, String userId);

    /**
     * 发送团购结果
     */
    void sendGroupResult(Integer groupId, boolean success);

    /**
     * 检查用户是否已加入团购
     * @param groupId 团购ID
     * @param userId 用户ID
     * @return 是否已加入
     */
    boolean checkUserJoined(Integer groupId, String userId);

    /**
     * 加入团购
     */
    boolean join(GroupBuying groupBuying);

    /**
     * 用户加入团购
     * @param groupId 团购ID
     * @param userId 用户ID
     */
    void join(Integer groupId, String userId);

    /**
     * 根据ID获取团购信息
     */
    GroupBuying getGroupById(Integer groupId);

    /**
     * 获取用户的团购订单列表
     * @param openid 用户openid
     * @return 团购订单列表
     */
    List<Map<String, Object>> getGroupOrders(String openid);
}