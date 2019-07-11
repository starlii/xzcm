package com.xzcmapi.service.impl;

import com.xzcmapi.entity.*;
import com.xzcmapi.mapper.PermissionMapper;
import com.xzcmapi.mapper.RoleMapper;
import com.xzcmapi.mapper.RolePermissionMapper;
import com.xzcmapi.service.LogService;
import com.xzcmapi.service.RoleService;
import com.xzcmapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService {

    private static final Integer BASE_VALUE_ID = 16;
    private static final Integer BASH_VALUE_SORT = 10000;
    private static final Integer BASE_VALUE_PID = 0;

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Autowired
    private LogService logService;
    @Autowired
    private UserService userService;

    @Transactional(readOnly = true)
    @Override
    public Role findByName(String name) {
        Role role = new Role();
        role.setName(name);
        return this.findOne(role);
    }


    @Override
    public List<Map<String, Object>> getAllUserByRoleId(Map<String,Object> params) {
        return roleMapper.getAllUserByRoleId(params);
    }

    @Override
    public List<Role> findByUserId(Long userId) {
        return roleMapper.findByUserId(userId);
    }

    @Override
    public Boolean saveRoleAndRolePermission(Role role, Long[] permissionIds) {
        int count = 0;
        int countList = 0;
        //保存角色信息
        count = this.save(role);
        //保存该角色所拥有的权限
        List<RolePermission> rolePermissionList = this.getRolePermissionList(role.getId(), permissionIds);
        if (rolePermissionList.size() > 0) {
            countList = rolePermissionMapper.insertList(rolePermissionList);
        }
        return count == 1;
    }

    @Override
    public Boolean distributePermissionByRoleId(Long roleId, Long[] permissionIds) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        rolePermissionMapper.delete(rolePermission);
        int count = 0;
        int countList = 0;
        List<RolePermission> rolePermissionList = this.getRolePermissionList(roleId,permissionIds);
        if (rolePermissionList.size() > 0) {
            countList = rolePermissionMapper.insertList(rolePermissionList);
        }
        return count == 1;
    }

    @Override
    public Boolean updateRoleAndRolePermission(Long id, Long[] permissionIds) {
        int count = 0;
        int countList = 0;
        //更新该角色所拥有的权限
        //先删除当前角色所拥有的权限再重现插入
        RolePermission delRolePermission = new RolePermission();
        delRolePermission.setRoleId(id);
        rolePermissionMapper.delete(delRolePermission);

        List<RolePermission> rolePermissionList = this.getRolePermissionList(id, permissionIds);
        if (rolePermissionList.size() > 0) {
            countList = rolePermissionMapper.insertList(rolePermissionList);
        }

        //清空认证和授权信息，使其重新加载
        //authenticationRealm.clearCache();
        return count == 1;
    }

    @Override
    public Boolean deleteRoleAndRolePermissionByRoleId(Long roleId) {
        Role role = this.findById(roleId);
        //删除角色
        int count1 = this.deleteById(roleId);

        //级联删除该角色所关联的权限
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        rolePermissionMapper.delete(rolePermission);
        return count1 == 1;
    }

    /**
     * 封装角色和权限的关系并返回
     *
     * @param roleId
     * @param permissionIds
     * @return
     */
    private List<RolePermission> getRolePermissionList(Long roleId, Long[] permissionIds) {
        List<RolePermission> rolePermissionList = new ArrayList<RolePermission>();
        RolePermission rolePermission = null;
        if (permissionIds == null) {
            return new ArrayList<RolePermission>();
        }
        for (Long permissionId : permissionIds) {
            rolePermission = new RolePermission();
            rolePermission.setPermissionId(permissionId);
            rolePermission.setRoleId(roleId);
            rolePermission.setCreateTime(new Date());
            rolePermissionList.add(rolePermission);
        }
        return rolePermissionList;
    }

    @Override
    public boolean checkUserManagerPermission(Long userId) {
//        List<Role> byUserId = roleMapper.findByUserId(userId);
//        if(byUserId == null || byUserId.isEmpty())
//            return false;
//        for (Role role : byUserId) {
//            Integer isManager = role.getIsManager();
//            if(isManager.equals(Role.Manager.MANAGER.getValue()))
//                return true;
//        }
//        return false;
        User user = userService.findById(userId);
        if (user.getRole().equals(User.Role.SYSTEM.getRemark()))
            return true;
        return false;
    }

    @Override
    public Long getUserPermission(Long userId) {
        List<Role> byUserId = roleMapper.findByUserId(userId);
        if(byUserId == null || byUserId.isEmpty())
            return null;
        return byUserId.get(0).getId();
    }
}
