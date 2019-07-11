package com.xzcmapi.service.impl;

import com.xiaoleilu.hutool.util.StrUtil;
import com.xzcmapi.entity.RolePermission;
import com.xzcmapi.service.RolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class RolePermissionServiceImpl extends BaseServiceImpl<RolePermission> implements RolePermissionService {

    @Transactional(readOnly = true)
    @Override
    public List<Long> findListPermissionIdsByRoleId(Long roleId) throws Exception {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        List<RolePermission> rolePermissionList = this.findListByWhere(rolePermission);
        List<Long> permissionIds = new ArrayList<Long>();
        for (RolePermission permission : rolePermissionList) {
            permissionIds.add(permission.getPermissionId());
        }
        return permissionIds;
    }

    @Transactional(readOnly = true)
    @Override
    public String findPermissionIdsByRoleId(Long roleId) throws Exception {
        List<Long> permissionIds = this.findListPermissionIdsByRoleId(roleId);
//        StringUtils.join(permissionIds, ",");
        return  StrUtil.join(",", permissionIds);
    }
}
