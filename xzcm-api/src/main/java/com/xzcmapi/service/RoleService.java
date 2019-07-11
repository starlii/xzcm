package com.xzcmapi.service;


import com.xzcmapi.entity.Role;

import java.util.List;
import java.util.Map;

public interface RoleService extends BaseService<Role>{
    /**
     * 根据角色名称查询角色对象信息
     * @param name
     * @return
     */
    Role findByName(String name);


    /**
     * 根据用户ID查询角色对象信息
     * @param userId
     * @return
     */
    List<Role> findByUserId(Long userId);
    /**
     *获取角色下的所有用户
     * */
    List<Map<String,Object>> getAllUserByRoleId(Map<String, Object> params);
    /**
     * 保存角色信息并级联关联所拥有的权限
     * @param role  角色信息
     * @param permissionIds 权限集合
     * @return
     */
    Boolean saveRoleAndRolePermission(Role role, Long[] permissionIds);
    /**
     * 通过角色ID分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限集合
     * */
    Boolean distributePermissionByRoleId(Long roleId, Long[] permissionIds);
    /**
     * 更新角色信息并级联关联所拥有的权限
     * @param permissionIds
     * @return
     */
    Boolean updateRoleAndRolePermission(Long id, Long[] permissionIds);
    /**
     * 根据角色ID删除角色并级联删除该角色和权限的关联信息
     * @param roleId
     * @return
     */
    Boolean deleteRoleAndRolePermissionByRoleId(Long roleId);

    boolean checkUserManagerPermission(Long userId);

    Long getUserPermission(Long userId);
}
