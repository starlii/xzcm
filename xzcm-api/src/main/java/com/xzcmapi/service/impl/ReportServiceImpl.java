package com.xzcmapi.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzcmapi.entity.GiftLog;
import com.xzcmapi.entity.ReportForm;
import com.xzcmapi.entity.User;
import com.xzcmapi.entity.VoteLog;
import com.xzcmapi.mapper.ReportFormMapper;
import com.xzcmapi.mapper.TransactionMapper;
import com.xzcmapi.model.ChargeModel;
import com.xzcmapi.model.ReportSearchModel;
import com.xzcmapi.model.SettlementModel;
import com.xzcmapi.model.TodayDealsModel;
import com.xzcmapi.service.ActivityService;
import com.xzcmapi.service.ReportService;
import com.xzcmapi.service.UserService;
import com.xzcmapi.util.TimeSearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private ReportFormMapper reportFormMapper;
    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Override
    public TodayDealsModel getTodayDeals(Long userId,Integer self) {
        List<GiftLog> giftLog = activityService.getGiftLog(null, userId,true,self);
        List<VoteLog> voteLog = activityService.getVoteLog(null, userId,self);
        giftLog = giftLog.stream().limit(20).collect(Collectors.toList());
        voteLog = voteLog.stream().limit(20).collect(Collectors.toList());
        TodayDealsModel todayDealsModel = new TodayDealsModel();
        todayDealsModel = activityService.getTodayDeals(userId,self);
        todayDealsModel.setGiftLogs(giftLog);
        todayDealsModel.setVoteLogs(voteLog);
        return todayDealsModel;
    }

    @Override
    public PageInfo<List<SettlementModel>> settlement(ReportSearchModel reportSearchModel, Long userId) {
        User user = userService.findById(userId);
        Integer timeFlag = reportSearchModel.getTimeFlag();
        Date startTime = reportSearchModel.getStartTime();
        Date endTime = reportSearchModel.getEndTime();
        if(timeFlag != null){
            List<Date> startAndEndDate = TimeSearchUtil.getStartAndEndDate(timeFlag);
            startTime = startAndEndDate.get(0);
            endTime = startAndEndDate.get(1);
        }
        PageHelper.startPage(reportSearchModel.getPage() != null?reportSearchModel.getPage():1,reportSearchModel.getSize() != null?reportSearchModel.getSize():10);
        List<SettlementModel> settlement = reportFormMapper.settlement(startTime, endTime, reportSearchModel.getUsername(), userId,user.getRoleValue());
        return new PageInfo(settlement);
    }

    @Override
    public PageInfo<List<ChargeModel>> charge(ReportSearchModel reportSearchModel, Long userId) {
        User user = userService.findById(userId);
        Integer timeFlag = reportSearchModel.getTimeFlag();
        Date startTime = reportSearchModel.getStartTime();
        Date endTime = reportSearchModel.getEndTime();
        if(timeFlag != null){
            List<Date> startAndEndDate = TimeSearchUtil.getStartAndEndDate(timeFlag);
            startTime = startAndEndDate.get(0);
            endTime = startAndEndDate.get(1);
        }
        PageHelper.startPage(reportSearchModel.getPage() != null?reportSearchModel.getPage():1,reportSearchModel.getSize() != null?reportSearchModel.getSize():10);
        List<ChargeModel> charge = reportFormMapper.charge(startTime, endTime, reportSearchModel.getUsername(), userId,user.getRoleValue());
        return new PageInfo(charge);
    }

    @Override
    public Integer updateStatus(Long id, Long userId) {
        ReportForm reportForm = reportFormMapper.selectByPrimaryKey(id);
        Integer status = reportForm.getStatus();
        if(status == 1)
            reportForm.setStatus(0);
        if (status == 0)
            reportForm.setStatus(1);
        return reportFormMapper.updateByPrimaryKeySelective(reportForm);
    }
}
