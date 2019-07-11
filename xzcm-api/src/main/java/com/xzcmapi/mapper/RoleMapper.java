package com.xzcmapi.mapper;

import com.xzcmapi.entity.Role;
import com.xzcmapi.util.MyMapper;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends MyMapper<Role> {
    /**
     * 根据用户ID查询角色对象信息
     * @param userId
     * @return
     */
    List<Role> findByUserId(Long userId);
    /**
     * 获取角色下的所有用户
     * */
    List<Map<String,Object>> getAllUserByRoleId(Map<String, Object> params);

}
