package com.xzcmapi.mapper;

import com.xzcmapi.entity.Report;
import com.xzcmapi.service.BaseService;
import com.xzcmapi.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReportMapper extends MyMapper<Report> {
    List<Report> get(@Param("activityId")Long activityId,
                     @Param("userId")Long userId,
                     @Param("role")String role);
}
