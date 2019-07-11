package com.xzcmapi.mapper;

import com.xzcmapi.model.UserModel;
import com.xzcmapi.util.MyMapper;
import com.xzcmapi.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends MyMapper<User> {

    List<UserModel> get(@Param(value = "username")String username,
                        @Param("userId")Long userId,
                        @Param("id")Long id,@Param("userPackage") Integer userPackage,
                        @Param("role")String role);

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    User findByUserName(String username);


    /**
     *
     * @param userId
     * @param roleId
     * @return
     */
    Integer saveUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);


}
