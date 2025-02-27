package com.mall.controller;

import com.mall.entity.GroupBuying;
import com.mall.entity.R;
import com.mall.service.IGroupBuyingService;
import com.mall.util.JwtUtils;
import com.mall.util.DateUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/groupBuying")
public class GroupBuyingController {
    private static final Logger log = LoggerFactory.getLogger(GroupBuyingController.class);

    @Autowired
    private IGroupBuyingService groupBuyingService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 获取商品的团购信息
     */
    @GetMapping("/info")
    public R info(@RequestParam Integer productId) {
        log.info("查询商品[{}]的团购信息", productId);
        try {
            if (productId == null) {
                return R.error("参数错误");
            }

            GroupBuying info = groupBuyingService.getActiveGroupByProductId(productId);
            log.info("商品[{}]的团购信息: {}", productId, info);
            return R.ok().put("data", info);

        } catch (Exception e) {
            log.error("获取团购信息异常", e);
            return R.error("系统异常:" + e.getMessage());
        }
    }

    /**
     * 检查用户是否已参与团购
     */
    @GetMapping("/checkJoined")
    public R checkJoined(@RequestParam Integer groupId, HttpServletRequest request) {
        try {
            // 校验groupId参数
            if (groupId == null || groupId <= 0) {
                log.error("参数错误: groupId不能为空或非正数");
                return R.error("参数错误");
            }

            // 获取用户ID
            String userId = jwtUtils.getTokenUserId(request);
            if (userId == null) {
                log.warn("未登录用户访问checkJoined接口");
                return R.error("未登录");
            }

            log.info("检查用户[{}]是否参与团购[{}]", userId, groupId);
            boolean joined = groupBuyingService.checkUserJoined(groupId, userId);
            return R.ok().put("joined", joined);

        } catch (Exception e) {
            log.error("检查参与状态异常 - groupId: {}, 错误: {}", groupId, e.getMessage(), e);
            return R.error("服务器繁忙，请稍后重试");
        }
    }

    /**
     * 加入团购
     */
    @PostMapping("/join")
    public R join(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            String token = request.getHeader("token");
            Claims claims = jwtUtils.validateJWT(token).getClaims();
            String userId = claims.getId();
            Integer groupId = (Integer) params.get("groupId");

            log.info("用户[{}]请求加入团购[{}]", userId, groupId);

            // 调用 service 处理加入团购逻辑
            Map<String, Object> orderInfo = groupBuyingService.joinGroup(groupId, userId);

            // 确保返回必要的订单信息
            if (orderInfo != null && orderInfo.containsKey("orderNo")) {
                return R.ok()
                        .put("orderNo", orderInfo.get("orderNo"))
                        .put("totalPrice", orderInfo.get("totalPrice"))
                        .put("productInfo", orderInfo.get("productInfo")); // 添加商品信息
            } else {
                return R.error("创建订单失败，缺少订单信息");
            }
        } catch (Exception e) {
            log.error("加入团购异常", e);
            return R.error("加入团购失败: " + e.getMessage());
        }
    }

    /**
     * 获取团购详情
     */
    @GetMapping("/detail/{groupId}")
    public R getGroupDetail(@PathVariable Integer groupId) {
        try {
            GroupBuying groupBuying = groupBuyingService.getGroupDetail(groupId);
            if (groupBuying == null) {
                return R.error("团购不存在");
            }
            return R.ok(groupBuying);
        } catch (Exception e) {
            log.error("获取团购详情异常", e);
            return R.error("获取团购详情失败:" + e.getMessage());
        }
    }

    /**
     * 获取用户的团购订单列表
     */
    @GetMapping("/orders")
    public R getGroupOrders(HttpServletRequest request) {
        // 从请求头获取token
        String token = request.getHeader("authorization");
        if (StringUtils.isEmpty(token)) {
            return R.error("请先登录");
        }

        try {
            // 从token中获取用户信息
            Claims claims = jwtUtils.validateJWT(token).getClaims();
            if (claims == null) {
                return R.error("无效的token，请重新登录");
            }
            String openid = claims.getId();

            // 获取团购订单列表
            List<Map<String, Object>> orders = groupBuyingService.getGroupOrders(openid);

            if (orders != null) {
                // 处理日期格式和状态
                orders.forEach(order -> {
                    // 处理时间格式
                    if (order.get("startTime") != null) {
                        order.put("startTime", DateUtil.formatDateTime((Date) order.get("startTime")));
                    }
                    if (order.get("endTime") != null) {
                        order.put("endTime", DateUtil.formatDateTime((Date) order.get("endTime")));
                    }

                    // 处理价格
                    if (order.get("groupPrice") != null) {
                        order.put("groupPrice", new BigDecimal(order.get("groupPrice").toString()).setScale(2, RoundingMode.HALF_UP));
                    }
                    if (order.get("originalPrice") != null) {
                        order.put("originalPrice", new BigDecimal(order.get("originalPrice").toString()).setScale(2, RoundingMode.HALF_UP));
                    }
                });
            }

            return R.ok().put("orders", orders);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("获取订单失败");
        }
    }
}