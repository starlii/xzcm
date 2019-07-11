package com.xzcmapi.controller.system;

import com.github.pagehelper.PageInfo;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.model.ScheduleJobLog;
import com.xzcmapi.service.ScheduleJobLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/job/log")
@Api(description = "任务日志")
public class JobLogController extends BaseController {

    private static final String BASE_PATH = "system/job/";

    @Resource
    private ScheduleJobLogService scheduleJobLogService;

    /**
     * 根据ID分页查询调度任务历史
     *
     * @param pageNum   当前页码
     * @param jobName  用户名
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @GetMapping("/{jobId}")
    @ApiOperation(value = "根据ID分页查询调度任务历史",notes = "根据ID分页查询调度任务历史")
    public ResponseResult list(
            @RequestParam(value = "pageNum", defaultValue = "1") @ApiParam(value = "当前页码") Integer pageNum,
            @PathVariable("jobId") @ApiParam(value = "任务ID") Long jobId,
            @RequestParam @ApiParam(value = "任务名") String jobName,
            @RequestParam @ApiParam(value = "开始时间") String startTime,
            @RequestParam @ApiParam(value = "结束时间") String endTime) {
        log.debug("根据ID分页查询调度任务历史参数! pageNum = {}, username = {}, username = {}, startTime = {}, endTime = {}", pageNum, jobName, startTime, endTime);
        PageInfo<ScheduleJobLog> pageInfo = scheduleJobLogService.findPage(pageNum, PAGESIZE, jobId, jobName, startTime, endTime);
        log.info("根据ID分页查询调度任务历史结果！ pageInfo = {}", pageInfo);
        return new ResponseResult(false,SUCCESS_LOAD_MESSAGE,pageInfo);
    }
}
