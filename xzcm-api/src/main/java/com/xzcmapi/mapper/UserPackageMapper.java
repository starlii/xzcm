package com.xzcmapi.mapper;

import com.xzcmapi.entity.UserPackage;
import com.xzcmapi.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserPackageMapper extends MyMapper<UserPackage> {
    List<UserPackage> get(@Param("id") Long id ,
                          @Param("userId") Long userId,
                          @Param("role")String role);
}
