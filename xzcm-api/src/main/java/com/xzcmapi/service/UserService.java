package com.xzcmapi.service;

import com.github.pagehelper.PageInfo;
import com.xzcmapi.entity.User;
import com.xzcmapi.model.PageParam;
import com.xzcmapi.model.UserModel;

import java.util.List;

public interface UserService extends BaseService<User>{

    PageInfo<List<UserModel>> get(String username,PageParam pageParam,Long userId,Long id,Integer userPackage);

    Boolean saveUser(UserModel userModel, Long userId);
    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
     User findByUserName(String username) throws Exception;

    /**
     * 保存用户信息和关联用户和角色
     * @param user    用户对象
     * @param roleId  角色ID
     */
    Boolean saveUserAndUserRole(User user, Long roleId) throws Exception;


    /**
     * 批量给用户分配角色
     * @param userIds
     * @param roleIds
     * @return
     */
    Boolean saveUserRole(List<Long> userIds, List<Long> roleIds, Long creatorId);

    /**
     * 修改管理员
     * @param user
     * @return
     */
    Boolean updateUser(UserModel user);

    void deleteUser(User user);

}
