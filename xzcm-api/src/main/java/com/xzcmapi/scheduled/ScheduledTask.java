package com.xzcmapi.scheduled;


import com.xzcmapi.service.ActivityService;
import com.xzcmapi.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableScheduling
@Transactional
public class ScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private ActivityService activityService;

    @Autowired
    private PlayerService playerService;


    /**
     * 定时更新活动状态 10秒钟更新一次
     */
//    @Scheduled(cron = "*/10 * * * * ? ")
    public void updateActivityStatus(){
        activityService.updateActivityStatus();
    }

    /**
     * 定时更新选手的锁定状态 1秒更新一次
     */
    @Scheduled(cron = "*/1 * * * * ? ")
    public void updateLockStatus(){
        playerService.updateLockStatus();
    }

    /**
     * 每天十点更新今日之星
     */
//    @Scheduled(cron = "0 0 10 * * ?")
    public void updateTodayStar(){
        activityService.updateTodayStar();
    }
}
