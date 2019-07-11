package com.xzcmapi.service;

import com.xzcmapi.entity.ReportForm;
import com.xzcmapi.service.impl.BaseServiceImpl;

public interface ReportFormService extends BaseService<ReportForm> {

    ReportForm getDaySett(Long userId);

    ReportForm getDayCharge(Long userId);
}
