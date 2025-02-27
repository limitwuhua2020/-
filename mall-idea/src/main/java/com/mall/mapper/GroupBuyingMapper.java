package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.GroupBuying;
import com.mall.entity.GroupBuyingMember;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface GroupBuyingMapper extends BaseMapper<GroupBuying> {
    // 查询用户参与次数
    Integer selectJoinCount(
            @Param("groupId") Integer groupId,
            @Param("userId") String userId
    );

    void insertMember(GroupBuyingMember member);

    List<GroupBuying> getActiveGroups();

    /**
     * 获取商品当前进行中的团购
     */
    GroupBuying getActiveGroupByProductId(@Param("productId") Integer productId);

    /**
     * 检查用户是否已参与团购
     */
    boolean checkUserJoined(@Param("groupId") Integer groupId, @Param("userId") String userId);

    /**
     * 获取团购成员列表
     */
    List<GroupBuyingMember> getGroupMembers(@Param("groupId") Integer groupId);

    GroupBuying findById(Integer id);
    boolean isUserJoined(Integer groupId, String userId);

    /**
     * 获取商品最近完成的团购
     */
    @Select("SELECT * FROM t_group_buying WHERE productId = #{productId} AND status = 1 " +
            "ORDER BY endTime DESC LIMIT 1")
    GroupBuying getLatestCompletedGroupByProductId(@Param("productId") Integer productId);

    List<Map<String, Object>> selectMembersByUserId(String userId);

    /**
     * 根据openid查询用户的团购订单
     * @param openid 用户openid
     * @return 团购订单列表
     */
    List<Map<String, Object>> selectMembersByOpenid(String openid);
}