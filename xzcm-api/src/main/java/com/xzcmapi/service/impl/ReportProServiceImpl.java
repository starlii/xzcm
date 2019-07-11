package com.xzcmapi.service.impl;

import com.xiaoleilu.hutool.http.HttpUtil;
import com.xzcmapi.entity.Report;
import com.xzcmapi.entity.User;
import com.xzcmapi.mapper.ReportMapper;
import com.xzcmapi.service.ReportProService;
import com.xzcmapi.service.UserService;
import com.xzcmapi.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class ReportProServiceImpl extends BaseServiceImpl<Report> implements ReportProService {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private UserService userService;


    @Override
    public Report add(HttpServletRequest request, Report report) {
        String clientIP = HttpUtil.getClientIP(request);
        Report dbReport = new Report();
        dbReport.setReportType(report.getReportType());
        dbReport.setActivityId(report.getActivityId());
        dbReport.setPlayerId(report.getPlayerId());
        dbReport.setIp(clientIP + " " + IpUtil.getCity(clientIP));
        dbReport = findOne(dbReport);
        if(dbReport != null){
            dbReport.setReportTimes((dbReport.getReportTimes() == null?1:dbReport.getReportTimes()) + 1);
            updateSelective(dbReport);
            return dbReport;
        }
        report.setIp(clientIP);
        report.setCreateTime(new Date());
        report.setReportTimes(1);
        if(report.getReportType() != null)
            report.setReportContent(Report.getReportContent(report.getReportType()));
        save(report);
        return report;
    }

    @Override
    public List<Report> get(Long activityId,Long userId) {
        User user = userService.findById(userId);
        String roleValue = user.getRoleValue();
        return reportMapper.get(activityId,userId,roleValue);
    }
}
