package com.xzcmapi.mapper;

import com.xzcmapi.entity.Activity;
import com.xzcmapi.model.*;
import com.xzcmapi.util.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.Date;
import java.util.List;

public interface ActivityMapper extends MyMapper<Activity> {

    List<ActivityModel> get(@Param("search")String search,
                            @Param("time")Date time,
                            @Param("status")Integer status,
                            @Param("userId") Long userId,
                            @Param("role")String role,
                            @Param("isSelf")Boolean isSelf,
                            @Param("today")Integer today);

    List<ActivityDateModel> getAllActivityDate();

    List<ActivityStarModel> getActivityStar();

    H5HomePageModel getH5HomePage(@Param("activityId") Long activityId);

    List<DeleteFileModel> getDeleteModel(@Param("userId")Long userId,
                                         @Param("role")String role);
}
