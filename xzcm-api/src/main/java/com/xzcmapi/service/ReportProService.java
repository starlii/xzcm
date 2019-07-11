package com.xzcmapi.service;

import com.xzcmapi.entity.Report;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ReportProService extends BaseService<Report> {

    Report add(HttpServletRequest request, Report report);

    List<Report> get(Long activityId,Long userId);
}
