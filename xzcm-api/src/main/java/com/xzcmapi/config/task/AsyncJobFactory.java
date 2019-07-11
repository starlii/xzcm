package com.xzcmapi.config.task;

import com.xzcmapi.model.ScheduleJob;
import com.xzcmapi.util.task.ScheduleExecute;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 异步任务工厂(计划任务执行处 无状态)
 */
@Slf4j
public class AsyncJobFactory extends QuartzJobBean {

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("AsyncJobFactory execute");
		ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get(ScheduleJob.JOB_PARAM_KEY);

		//执行调度任务
		ScheduleExecute.execute(scheduleJob);
	}
}