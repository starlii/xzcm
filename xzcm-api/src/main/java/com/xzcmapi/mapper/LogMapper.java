package com.xzcmapi.mapper;

import com.xzcmapi.entity.Log;
import com.xzcmapi.util.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LogMapper extends MyMapper<Log> {
    /**
     *
     * @param username
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("SELECT id,create_time as createTime,creator as creator,app_name as appName,log_type as logType,username as username ,operation as operation,method_name as methodName,request_method as requestMethod,request_params as requestParams,request_ip as requestIp,request_uri as requestUri,exception_code as exceptionCode,exception_detail as exceptionDetail,time_consuming as timeConsuming,user_agent as userAgent FROM log WHERE ( username like concat('%',#{username},'%') and create_time between #{startTime} and #{endTime} ) order by create_time DESC ")
    List<Log> findPage(@Param(value = "username") String username, @Param(value = "startTime") String startTime, @Param(value = "endTime") String endTime);
}
