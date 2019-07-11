package com.xzcmapi.mapper;


import com.xzcmapi.entity.Permission;
import com.xzcmapi.model.TreeNode;
import com.xzcmapi.util.MyMapper;

import java.util.List;

public interface PermissionMapper extends MyMapper<Permission> {
    /**
     * 根据用户ID查询该用户所拥有的权限列表
     * @param UserId
     * @return
     */
    List<Permission> findListPermissionByUserId(Long userId);

    /**
     * 根据用户ID查询用户菜单列表
     * @param UserId
     * @return
     */
    List<Permission> findMenuListByUserId(Long userId);

    /**
     * 按角色id查询角色的权限信息
     * @param roleId
     * @return
     */
    List<Permission> findListPermissionByRoleId(Long roleId);

    /**
     * 返回树列表
     * @return
     */
    List<TreeNode> findTreeList();
}
