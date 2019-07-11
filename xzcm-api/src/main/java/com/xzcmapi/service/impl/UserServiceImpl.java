package com.xzcmapi.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzcmapi.common.CodeMessage;
import com.xzcmapi.common.Status;
import com.xzcmapi.common.SysParam;
import com.xzcmapi.entity.Role;
import com.xzcmapi.entity.User;
import com.xzcmapi.entity.UserRole;
import com.xzcmapi.exception.XzcmBaseRuntimeException;
import com.xzcmapi.mapper.RoleMapper;
import com.xzcmapi.mapper.UserMapper;
import com.xzcmapi.model.PageParam;
import com.xzcmapi.model.UserModel;
import com.xzcmapi.service.LogService;
import com.xzcmapi.service.RoleService;
import com.xzcmapi.service.UserRoleService;
import com.xzcmapi.service.UserService;
import com.xzcmapi.util.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Transactional
@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;


    @Override
    public PageInfo<List<UserModel>> get(String username, PageParam pageParam,Long userId,Long id,Integer userPackage) {
//        boolean isManage = roleService.checkUserManagerPermission(userId);
//        if(isManage)
//            userId = null;
        User user = findById(userId);
        PageHelper.startPage(pageParam.getPage(),pageParam.getSize());
        return new PageInfo(userMapper.get(username,userId,id,userPackage,user.getRoleValue()));
    }

    @Transactional(readOnly=true)
    @Override
    public User findByUserName(String username) {
        return userMapper.findByUserName(username);
    }

    @Override
    public Boolean saveUser(UserModel userModel, Long userId) {
        if(Objects.nonNull(findByUserName(userModel.getUsername()))){
            return false;
        }
        User creator = findById(userId);
        String role = creator.getRole();
        Integer creatorPackage = User.getRoleValue(role);
        Integer userPackage = userModel.getRole();
        if(userPackage == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PARAMSERROR,"role must not be null.");

        if(creatorPackage == null)
            throw new XzcmBaseRuntimeException(CodeMessage.PEMISSIONERROR,"创建者权限异常！");

        if(creatorPackage != 0 && creatorPackage >= userPackage)
            throw new XzcmBaseRuntimeException(CodeMessage.PEMISSIONERROR,"权限不足，请使用超级管理或者上级权限用户创建此用户！");

        //獲取roe_value ，system 创建 system 用户  则生成新的role_value
        String roleValue = null;
        if(creatorPackage == 0 && creatorPackage.equals(userPackage)){
            roleValue = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        } else {
            roleValue = creator.getRoleValue() + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        }

        //创建用户
        User user = new User();
        BeanUtils.copyProperties(userModel,user);
        user.setId(null);
        user.setRole(User.getRoleRemark(userPackage));
        user.setRoleValue(roleValue);
        user.setPassword(PasswordUtil.encrypt(userModel.getPassword()));
        user.setCreator(userId);
        user.setStatus(userModel.getStatus());
        user.setUpdateTime(new Date());
        save(user);

//        //分配权限 1--超级管理员 2--管理员 3--普通用户
//        UserRole userRole = new UserRole();
//        userRole.setUserId(user.getId());
//        userRole.setRoleId((long) (userPackage + 1));
//        userRole.setCreator(userId);
//        userRole.setCreateTime(new Date());
//        userRoleService.save(userRole);
        return true;
    }

    @Override
    public Boolean saveUserAndUserRole(User user, Long roleId) throws Exception{
        int count = 0;
        //加密
        user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        Role role = roleMapper.selectByPrimaryKey(roleId);
        count = this.save(user);

        //关联用户和角色信息
        UserRole userRole = new UserRole();
        userRole.setRoleId(roleId);
        userRole.setUserId(user.getId());
        userRole.setCreator(user.getCreator());
        count = userRoleService.save(userRole);

        return count == 1;
    }


    @Override
    public Boolean saveUserRole(List<Long> userIds, List<Long> roleIds,Long creatorId) {
        StringBuilder stringBuilder = new StringBuilder();
        UserRole userRole;
        for(Long userId : userIds){
            User user = this.findById(userId);
            userRole = new UserRole();
            userRole.setUserId(userId);
            userRoleService.deleteByWhere(userRole);
            for(Long roleId : roleIds){
                userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setCreator(creatorId);
                userRoleService.save(userRole);
            }
        }
        return true;
    }

    @Override
    public Boolean updateUser(UserModel user) {
        User u = findById(user.getId());
        User update = new User();
        update.setId(u.getId());
        if(Objects.equals(Status.ENABLE.getStatus(),user.getStatus()) || Objects.equals(Status.DISABLE.getStatus(),user.getStatus())){
            update.setStatus(user.getStatus());
        }
        update.setUserPackage(user.getUserPackage());
        update.setUsername(user.getUsername());
        update.setRole(user.getRole() != null?User.getRoleRemark(user.getRole()):null);
        update.setPassword(PasswordUtil.encrypt(user.getPassword()));
        update.setRemark(Objects.equals(user.getRemark(),u.getRemark()) ? null : user.getRemark());
        update.setUpdateTime(new Date());
        update.setRole(user.getRole() != null?User.getRoleRemark(user.getRole()):update.getRole());
        updateSelective(update);
        return true;
    }


    @Override
    public void deleteUser(User user) {
        Long deleteId = user.getId();
        UserRole userRole = new UserRole();
        userRole.setUserId(deleteId);
        userRoleService.deleteByWhere(userRole);
        deleteById(deleteId);

    }

}
