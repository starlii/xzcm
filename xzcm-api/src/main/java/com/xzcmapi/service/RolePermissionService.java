package com.xzcmapi.service;


import com.xzcmapi.entity.RolePermission;

import java.util.List;

public interface RolePermissionService extends BaseService<RolePermission>{
    /**
     * 根据角色ID获取当前角色下所有权限ID集合
     * @param roleId    角色ID
     * @return
     * @throws Exception
     */
    List<Long> findListPermissionIdsByRoleId(Long roleId) throws Exception;
    /**
     * 根据角色ID获取当前角色下所有权限ID集合，以,分割
     * @param roleId    角色ID
     * @return
     * @throws Exception
     */
    String findPermissionIdsByRoleId(Long roleId) throws Exception;
}
