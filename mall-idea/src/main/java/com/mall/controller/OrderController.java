package com.mall.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.entity.Order;
import com.mall.entity.OrderDetail;
import com.mall.entity.R;
import com.mall.service.IOrderDetailService;
import com.mall.service.IOrderService;
import com.mall.util.*;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;


/**
 * 订单Controller控制器
 */
@RestController
@RequestMapping("/my/order/")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IOrderDetailService orderDetailService;



    /**
     * 创建订单
     */
    @Transactional
    @RequestMapping("/create")
    public R create(@RequestBody Order order, @RequestHeader(value = "token") String token) {
        if (StringUtil.isEmpty(token)) {
            return R.error(500, "无权限访问！");
        }

        Claims claims = JwtUtils.validateJWT(token).getClaims();
        if (claims == null) {
            return R.error(500, "鉴权失败！");
        }

        String openId = claims.getId();
        order.setUserId(openId);
        order.setOrderNo("JAVA" + DateUtil.getCurrentDateStr());
        order.setCreateDate(new Date());
        order.setStatus(1); // 设置为待付款状态

        OrderDetail[] goods = order.getGoods();
        orderService.save(order);
        String orderNo = order.getOrderNo();

        for (OrderDetail orderDetail : goods) {
            orderDetail.setMId(order.getId());
            orderDetailService.save(orderDetail);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("orderNo", orderNo);
        resultMap.put("orderId", order.getId());
        return R.ok(resultMap);
    }


    /**
     * 模拟支付
     */
    // OrderController.java
    @PostMapping("/preparePay")
    public R preparePay(@RequestBody Map<String, Object> params, @RequestHeader(value = "token") String token) {
        log.info("收到预支付请求, 参数: {}", params);

        try {
            // 验证 token
            if (StringUtil.isEmpty(token)) {
                return R.error(500, "无权限访问！");
            }

            Claims claims = JwtUtils.validateJWT(token).getClaims();
            if (claims == null) {
                return R.error(500, "鉴权失败！");
            }

            // 验证参数
            if (!params.containsKey("orderNo") || StringUtil.isEmpty(params.get("orderNo").toString())) {
                return R.error(500, "订单号不能为空！");
            }
            String orderNo = params.get("orderNo").toString();

            // 获取订单
            Order order = orderService.getByOrderNo(orderNo);
            if (order == null) {
                return R.error(500, "订单不存在！");
            }

            // 验证订单所属用户
            String openId = claims.getId();
            if (!order.getUserId().equals(openId)) {
                return R.error(500, "无权操作此订单");
            }

            // 模拟微信支付所需的参数
            Map<String, Object> resultMap = new HashMap<>();
            Map<String, String> payParams = new HashMap<>();
            payParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            payParams.put("nonceStr", UUID.randomUUID().toString().replaceAll("-", ""));
            payParams.put("package", "prepay_id=" + orderNo);
            payParams.put("signType", "MD5");
            payParams.put("paySign", "模拟的签名"); // 实际项目中需要真实的签名

            resultMap.put("code", 0);
            resultMap.put("msg", "success");
            resultMap.put("data", payParams);
            return R.ok(resultMap);
        } catch (Exception e) {
            log.error("预支付请求处理失败", e);
            return R.error(500, "支付准备失败: " + e.getMessage());
        }
    }

    /**
     * 订单查询
     */
    @RequestMapping("/list")
    public R list(Integer type, Integer page, Integer pageSize, String sort, String order,
                  @RequestHeader(value = "token") String token) {
        log.info("获取订单列表, 参数: type={}, page={}, pageSize={}, sort={}, order={}",
                type, page, pageSize, sort, order);

        try {
            // 验证token
            if (StringUtil.isEmpty(token)) {
                return R.error(500, "无权限访问！");
            }
            Claims claims = JwtUtils.validateJWT(token).getClaims();
            if (claims == null) {
                return R.error(500, "鉴权失败！");
            }
            String openId = claims.getId();

            // 构建查询条件
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", openId);
            if (type != 0) {
                queryWrapper.eq("status", type);
            }

            // 默认按创建时间降序
            queryWrapper.orderByDesc("createDate");
            if (StringUtil.isNotEmpty(sort)) {
                queryWrapper.orderBy(true, "desc".equalsIgnoreCase(order), sort);
            }

            // 执行分页查询
            Page<Order> pageOrder = new Page<>(page, pageSize);
            Page<Order> orderResult = orderService.page(pageOrder, queryWrapper);

            // 构建返回结果
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("total", orderResult.getTotal());
            resultMap.put("totalPage", orderResult.getPages());
            resultMap.put("page", page);
            resultMap.put("orderList", orderResult.getRecords());

            return R.ok(resultMap);
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return R.error("获取订单列表失败");
        }
    }

    /**
     * 更新订单状态
     */
    @PostMapping("/updateStatus")
    public R updateStatus(@RequestBody Map<String, Object> params, @RequestHeader(value = "token") String token) {
        log.info("收到更新订单状态请求, 参数: {}", params);
        
        try {
            // 验证token
            if (StringUtil.isEmpty(token)) {
                return R.error(500, "无权限访问！");
            }

            Claims claims = JwtUtils.validateJWT(token).getClaims();
            if (claims == null) {
                return R.error(500, "鉴权失败！");
            }

            // 验证参数
            if (params.get("id") == null || params.get("status") == null) {
                return R.error(500, "参数不完整");
            }

            Integer orderId = Integer.parseInt(params.get("id").toString());
            Integer status = Integer.parseInt(params.get("status").toString());

            // 获取订单
            Order order = orderService.getById(orderId);
            if (order == null) {
                return R.error(500, "订单不存在");
            }

            // 验证订单所属用户
            String openId = claims.getId();
            if (!order.getUserId().equals(openId)) {
                return R.error(500, "无权操作此订单");
            }

            // 更新订单状态
            order.setStatus(status);
            if (status == 2) { // 支付成功
                order.setPayDate(new Date());
            }
            
            boolean success = orderService.updateById(order);
            if (!success) {
                return R.error(500, "更新订单状态失败");
            }
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("code", 0);
            resultMap.put("msg", "success");
            return R.ok(resultMap);
        } catch (Exception e) {
            log.error("更新订单状态失败", e);
            return R.error(500, "更新订单状态失败: " + e.getMessage());
        }
    }
}
