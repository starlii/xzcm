package com.xzcmapi.service.impl;

import com.xzcmapi.entity.ReportForm;
import com.xzcmapi.mapper.ReportFormMapper;
import com.xzcmapi.service.ReportFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportFormServiceImpl extends BaseServiceImpl<ReportForm> implements ReportFormService {

    @Autowired
    private ReportFormMapper reportFormMapper;

    @Override
    public ReportForm getDaySett(Long userId) {
        return reportFormMapper.getDaySett(userId);
    }

    @Override
    public ReportForm getDayCharge(Long userId) {
        return reportFormMapper.getDayCharge(userId);
    }
}
