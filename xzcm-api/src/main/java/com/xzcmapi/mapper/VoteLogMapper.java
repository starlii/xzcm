package com.xzcmapi.mapper;

import com.xzcmapi.entity.GiftLog;
import com.xzcmapi.entity.VoteLog;
import com.xzcmapi.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VoteLogMapper extends MyMapper<VoteLog> {

    Integer getUserLimit(@Param("openId")String openId,
                         @Param("playerId")Long playerId,
                         @Param("activityId")Long activityId);

    Integer getDayLimit(@Param("openId")String openId,
                        @Param("activityId")Long activityId);

    Integer getUserVoteSum(@Param("openId")String openId,
                           @Param("activityId")Long activityId);

    Integer voteInternal(@Param("openId")String openId);

    List<VoteLog> getVoteLog(@Param("activityId")Long activityId,
                             @Param("userId")Long userId,
                             @Param("role")String role,
                             @Param("self")Integer self);
}
