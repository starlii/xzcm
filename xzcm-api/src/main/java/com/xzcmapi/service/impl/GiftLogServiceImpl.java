package com.xzcmapi.service.impl;

import com.xzcmapi.entity.GiftLog;
import com.xzcmapi.mapper.GiftLogMapper;
import com.xzcmapi.service.GiftLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GiftLogServiceImpl extends BaseServiceImpl<GiftLog> implements GiftLogService {

    @Autowired
    private GiftLogMapper giftLogMapper;

    @Override
    public Integer getTodayActivity(Long userId) {
        return giftLogMapper.getTodayActivity(userId);
    }
}
