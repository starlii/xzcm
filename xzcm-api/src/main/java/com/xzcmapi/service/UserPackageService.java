package com.xzcmapi.service;

import com.xzcmapi.entity.UserPackage;

import java.util.List;

public interface UserPackageService extends BaseService<UserPackage>{

    List<UserPackage> get(Long id ,Long userId);
}
