package com.xzcmapi.service.impl;

import com.xzcmapi.entity.User;
import com.xzcmapi.entity.UserPackage;
import com.xzcmapi.mapper.UserPackageMapper;
import com.xzcmapi.service.UserPackageService;
import com.xzcmapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPackageServiceImpl extends BaseServiceImpl<UserPackage> implements UserPackageService {

    @Autowired
    private UserPackageMapper userPackageMapper;
    @Autowired
    private UserService userService;

    @Override
    public List<UserPackage> get(Long id, Long userId) {
        User byId = userService.findById(userId);
        return userPackageMapper.get(id,userId,byId.getRoleValue());
    }
}
