package com.xzcmapi.service;

import com.github.pagehelper.PageInfo;
import com.xzcmapi.entity.Log;

public interface LogService extends BaseService<Log> {

    /**
     * 分页查询日志列表
     * @param pageNum
     * @param pageSize
     * @param username
     * @param startTime
     * @param endTime
     * @return
     */
    PageInfo<Log> findPage(Integer pageNum, Integer pageSize, String username, String startTime, String endTime);

    /**
     * 保存日志内容
     * @param log
     */
    void saveLog(Log log);
}
