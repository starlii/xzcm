package com.xzcmapi.service;

import com.github.pagehelper.PageInfo;
import com.xzcmapi.entity.User;
import com.xzcmapi.model.ScheduleJob;

public interface ScheduleJobService extends BaseService<ScheduleJob> {

    /**
     * 初始化定时任务
     */
    void initScheduleJob();

    /**
     * 分页查询任务调度列表
     *
     * @param pageNum
     * @param pageSize
     * @param jobName
     * @param startTime
     * @param endTime
     * @return
     */
    PageInfo<ScheduleJob> findPage(Integer pageNum, Integer pageSize, String jobName, String startTime, String endTime);

    /**
     * 保存定时任务
     */
    void saveScheduleJob(ScheduleJob scheduleJob, User user);

    /**
     * 更新定时任务
     */
    void updateScheduleJob(ScheduleJob scheduleJob, User user);

    /**
     * 删除定时任务
     */
    void deleteScheduleJob(Long jobIds);

    /**
     * 删除自动投票定时任务
     * @param playerId
     */
    void deleteScheduleJobForAutoVote(Long playerId);

    void pauseScheduleJobForAutoVote(Long playerId);

    /**
     * 暂停运行调度任务
     */
    void pauseJob(Long jobId);

    /**
     * 恢复运行调度任务
     */
    void resumeJob(Long jobId);

    /**
     * 运行一次调度任务
     */
    void runOnce(Long jobId);

    /**
     * 根据任务名称和任务分组查询任务
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    ScheduleJob findByJobNameAndJobGroup(String jobName, String jobGroup);

    /**
     * 暂停所有调度任务
     */
    void pauseAllJob();

    /**
     * 恢复所有调度任务
     */
    void resumAllJob();
}
