package com.xzcmapi.service;

import com.github.pagehelper.PageInfo;
import com.xzcmapi.model.ChargeModel;
import com.xzcmapi.model.ReportSearchModel;
import com.xzcmapi.model.SettlementModel;
import com.xzcmapi.model.TodayDealsModel;

import java.util.List;

public interface ReportService {
    TodayDealsModel getTodayDeals(Long userId,Integer self);

    PageInfo<List<SettlementModel>> settlement(ReportSearchModel reportSearchModel, Long userId);

    PageInfo<List<ChargeModel>> charge(ReportSearchModel reportSearchModel, Long userId);

    Integer updateStatus(Long id,Long userId);
}
