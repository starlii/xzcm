package com.xzcmapi.service;

import com.xzcmapi.entity.GiftLog;

public interface GiftLogService extends BaseService<GiftLog>{

    Integer getTodayActivity(Long userId);
}
