package com.xzcmapi.mapper;

import com.xzcmapi.model.ScheduleJob;
import com.xzcmapi.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScheduleJobMapper extends MyMapper<ScheduleJob> {
    /**
     * 注:通用mapper 不支持@PostConstruct
     * 查询所有任务
     * @return
     */
    List<ScheduleJob> findList();

    List<ScheduleJob> findAutoVoteByPlayerId(@Param("playerId")Long playerId);
}
