package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.GroupBuyingMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupBuyingMemberMapper extends BaseMapper<GroupBuyingMember> {

    /**
     * 检查用户是否已加入团购
     */
    boolean isUserJoined(@Param("groupId") Integer groupId, @Param("userId") String userId);

    /**
     * 根据团购ID查询成员
     */
    List<GroupBuyingMember> findByGroupId(@Param("groupId") Integer groupId);
}