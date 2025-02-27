package com.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.entity.GroupBuying;
import com.mall.entity.GroupBuyingMember;
import com.mall.entity.Order;
import com.mall.entity.Product;
import com.mall.mapper.GroupBuyingMapper;
import com.mall.service.IGroupBuyingService;
import com.mall.service.IOrderService;
import com.mall.service.IProductService;
import com.mall.service.IWxUserInfoService;
import com.mall.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class IGroupBuyingServiceImpl extends ServiceImpl<GroupBuyingMapper, GroupBuying> implements IGroupBuyingService {

    private static final Logger log = LoggerFactory.getLogger(IGroupBuyingServiceImpl.class);

    @Autowired
    private GroupBuyingMapper groupBuyingMapper;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IWxUserInfoService wxUserInfoService;

    @Override
    public GroupBuying getGroupById(Integer groupId) {
        return groupBuyingMapper.selectById(groupId);
    }

    @Override
    @Transactional
    public Map<String, Object> joinGroup(Integer groupId, String userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 检查团购状态
            GroupBuying groupBuying = getGroupById(groupId);
            if (groupBuying == null || groupBuying.getStatus() != 0) {
                result.put("success", false);
                result.put("message", "团购不存在或已结束");
                return result;
            }

            // 2. 检查是否已参与
            if (checkUserJoined(groupId, userId)) {
                result.put("success", false);
                result.put("message", "您已参与该团购");
                return result;
            }

            // 3. 检查人数是否已满
            if (groupBuying.getJoinedNumber() >= groupBuying.getRequiredNumber()) {
                result.put("success", false);
                result.put("message", "团购已满员");
                return result;
            }

            // 4. 添加团购成员记录
            GroupBuyingMember member = new GroupBuyingMember();
            member.setGroupBuyingId(groupId);
            member.setUserId(userId);
            member.setJoinTime(new Date());
            groupBuyingMapper.insertMember(member);

            // 5. 更新团购人数
            groupBuying.setJoinedNumber(groupBuying.getJoinedNumber() + 1);

            // 6. 如果人数已满，更新团购状态为已完成
            if (groupBuying.getJoinedNumber() >= groupBuying.getRequiredNumber()) {
                groupBuying.setStatus(1); // 已完成
            }

            groupBuyingMapper.updateById(groupBuying);

            // 7. 获取商品信息
            Product product = productService.getById(groupBuying.getProductId());

            // 8. 构建返回结果
            result.put("success", true);
            result.put("message", "加入团购成功");
            result.put("groupInfo", groupBuying);
            result.put("productInfo", product);
            result.put("currentNumber", groupBuying.getJoinedNumber());
            result.put("remainingNumber", groupBuying.getRequiredNumber() - groupBuying.getJoinedNumber());

            return result;
        } catch (Exception e) {
            log.error("加入团购异常", e);
            result.put("success", false);
            result.put("message", "加入团购失败：" + e.getMessage());
            return result;
        }
    }

    @Override
    public GroupBuying getGroupDetail(Integer groupId) {
        GroupBuying groupBuying = this.getById(groupId);
        if (groupBuying != null) {
            // 关联商品信息
            Product product = productService.getById(groupBuying.getProductId());
            groupBuying.setProduct(product);

            // 获取团购成员
            List<GroupBuyingMember> members = groupBuyingMapper.getGroupMembers(groupId);
            groupBuying.setMembers(members);
        }
        return groupBuying;
    }

    @Override
    public List<GroupBuying> getActiveGroups() {
        return groupBuyingMapper.getActiveGroups();
    }

    @Override
    @Transactional
    public void checkGroupStatus(Integer groupId) {
        GroupBuying group = getById(groupId);
        if (group == null || group.getStatus() != 0) {
            return;
        }

        // 检查是否到期
        if (new Date().after(group.getEndTime())) {
            // 只有在未达到最低成团人数时，才将团购状态设置为失败
            if (group.getJoinedNumber() < group.getRequiredNumber()) {
                group.setStatus(2); // 失败
            } else {
                group.setStatus(1); // 成功
            }
            updateById(group);

            // 发送结果通知
            sendGroupResult(groupId, group.getStatus() == 1);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> createGroupOrder(Integer groupId, String userId) {
        // 获取团购信息
        GroupBuying groupBuying = getById(groupId);
        if (groupBuying == null) {
            throw new RuntimeException("团购不存在");
        }

        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo("GB" + DateUtil.getCurrentDateStr());
        order.setCreateDate(new Date());
        order.setStatus(1); // 待支付
        order.setTotalPrice(groupBuying.getGroupPrice());
        order.setGroupId(groupId);
        order.setIsGroup(true);
        order.setGroupStatus(1); // 拼团中

        orderService.save(order);

        // 返回订单信息
        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("orderId", order.getId());

        return result;
    }

    @Override
    public void sendGroupResult(Integer groupId, boolean success) {
        // 获取团购信息
        GroupBuying group = getById(groupId);
        if (group == null) {
            return;
        }

        // 获取商品信息
        Product product = productService.getById(group.getProductId());
        if (product == null) {
            return;
        }

        // 获取参与用户
        List<GroupBuyingMember> members = groupBuyingMapper.getGroupMembers(groupId);
    }

    @Override
    public GroupBuying getActiveGroupByProductId(Integer productId) {
        log.info("查询商品[{}]的进行中团购", productId);

        GroupBuying groupBuying = groupBuyingMapper.getActiveGroupByProductId(productId);

        if (groupBuying == null) {
            log.info("商品[{}]当前没有进行中的团购", productId);
        } else {
            log.info("商品[{}]的团购信息: {}", productId, groupBuying);
        }

        return groupBuying;
    }

    @Override
    public boolean checkUserJoined(Integer groupId, String userId) {
        return groupBuyingMapper.checkUserJoined(groupId, userId);
    }

    @Override
    @Transactional
    public boolean join(GroupBuying groupBuying) {
        log.info("用户加入团购[{}]", groupBuying);
        try {
            return this.save(groupBuying);
        } catch (Exception e) {
            log.error("加入团购失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public GroupBuying createGroup(GroupBuying groupBuying) {
        // 验证商品
        Product product = productService.getById(groupBuying.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        groupBuying.setJoinedNumber(0);
        groupBuying.setStatus(0); // 假设 0 表示团购进行中
        this.save(groupBuying);
        return groupBuying;
    }

    @Override
    @Transactional
    public void join(Integer groupId, String userId) {
        log.info("用户[{}]加入团购[{}]", userId, groupId);
        try {
            // 1. 检查团购状态
            GroupBuying groupBuying = getGroupById(groupId);
            if (groupBuying == null || groupBuying.getStatus() != 0) {
                throw new RuntimeException("团购不存在或已结束");
            }

            // 2. 检查是否已参与
            if (checkUserJoined(groupId, userId)) {
                throw new RuntimeException("您已参与该团购");
            }

            // 3. 添加团购成员记录
            GroupBuyingMember member = new GroupBuyingMember();
            member.setGroupBuyingId(groupId);
            member.setUserId(userId);
            member.setJoinTime(new Date());
            groupBuyingMapper.insertMember(member);

            // 4. 更新团购人数
            groupBuying.setJoinedNumber(groupBuying.getJoinedNumber() + 1);
            this.updateById(groupBuying);
        } catch (Exception e) {
            log.error("用户[{}]加入团购[{}]失败", userId, groupId, e);
            throw new RuntimeException("加入团购失败", e);
        }
    }

    @Override
    public List<Map<String, Object>> getGroupOrders(String openid) {
        log.info("获取用户[{}]的团购订单列表", openid);
        try {
            // 调用 mapper 获取团购订单列表
            List<Map<String, Object>> orders = groupBuyingMapper.selectMembersByOpenid(openid);

            // 处理订单数据
            if (orders != null) {
                for (Map<String, Object> order : orders) {
                    // 处理时间格式
                    if (order.get("createTime") != null) {
                        order.put("createTime", DateUtil.formatDateTime((Date) order.get("createTime")));
                    }
                    if (order.get("endTime") != null) {
                        order.put("endTime", DateUtil.formatDateTime((Date) order.get("endTime")));
                    }

                    // 处理状态显示
                    Integer status = (Integer) order.get("status");
                    String statusText;
                    switch (status) {
                        case 0:
                            statusText = "进行中";
                            break;
                        case 1:
                            statusText = "已完成";
                            break;
                        case 2:
                            statusText = "已失败";
                            break;
                        default:
                            statusText = "未知状态";
                    }
                    order.put("statusText", statusText);
                }
            }

            return orders;
        } catch (Exception e) {
            log.error("获取用户[{}]的团购订单列表失败", openid, e);
            throw new RuntimeException("获取团购订单失败", e);
        }
    }
}