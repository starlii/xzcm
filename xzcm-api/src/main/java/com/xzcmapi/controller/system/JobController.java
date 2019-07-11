package com.xzcmapi.controller.system;

import com.github.pagehelper.PageInfo;
import com.xzcmapi.annotation.CurrentUser;
import com.xzcmapi.annotation.OperationLog;
import com.xzcmapi.common.ResponseResult;
import com.xzcmapi.controller.BaseController;
import com.xzcmapi.entity.User;
import com.xzcmapi.model.ScheduleJob;
import com.xzcmapi.service.ScheduleJobService;
import com.xzcmapi.service.UserService;
import com.xzcmapi.util.xss.XssHttpServletRequestWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/job")
@Api(description = "任务管理")
public class JobController extends BaseController {

    private static final String BASE_PATH = "system/job/";

    @Resource
    private ScheduleJobService scheduleJobService;
    @Resource
    private UserService userService;

    /**
     * 分页查询调度任务列表
     *
     * @param pageNum   当前页码
     * @param jobName   用户名
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "分页查询调度任务列表", notes = "分页查询调度任务列表")
    public ResponseResult list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            String jobName, String startTime, String endTime) {
        log.debug("分页查询调度任务列表参数! pageNum = {}, username = {}, username = {}, startTime = {}, endTime = {}", pageNum, jobName, startTime, endTime);
        PageInfo<ScheduleJob> pageInfo = scheduleJobService.findPage(pageNum, PAGESIZE, jobName, startTime, endTime);
        log.info("分页查询调度任务列表结果！ pageInfo = {}", pageInfo);
        return new ResponseResult(false, "", pageInfo);
    }

//    /**
//     * 跳转到调度任务添加页面
//     *
//     * @return
//     */
//    @GetMapping(value = "/add")
//    @ApiOperation(value = "跳转到调度任务添加页面", notes = "跳转到调度任务添加页面")
//    public String add(ModelMap modelMap) {
//        log.info("跳转到调度任务添加页面!");
//        return BASE_PATH + "job-add";
//    }

    /**
     * 添加调度任务
     *
     * @param scheduleJob
     * @return
     */
    @OperationLog(value = "添加调度任务")
    @PostMapping(value = "/save")
    @ApiOperation(value = "添加调度任务", notes = "添加调度任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseResult saveJob(HttpServletRequest request, @RequestBody ScheduleJob scheduleJob, @ApiIgnore @CurrentUser User user) {

        //获取不进行xss过滤的request对象
        HttpServletRequest orgRequest = XssHttpServletRequestWrapper.getOrgRequest(request);


        log.debug("添加调度任务参数! scheduleJob = {}", scheduleJob);

        //判断任务是否已经存在相同的jobName和jobGroup
        ScheduleJob record = scheduleJobService.findByJobNameAndJobGroup(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        if (null != record) {
            return new ResponseResult(true, "改调度任务已被注册！");
        }

        scheduleJob.setRemoteUrl(orgRequest.getParameter("remoteUrl"));
        scheduleJobService.saveScheduleJob(scheduleJob, user);

        log.info("添加调度任务成功! jobId = {}", scheduleJob.getId());
        return new ResponseResult(false, "添加成功！");
    }

//    /**
//     * 跳转到调度任务编辑页面
//     *
//     * @return
//     */
//    @GetMapping(value = "/edit/{id}")
//    @ApiOperation(value = "跳转到调度任务编辑页面", notes = "跳转到调度任务编辑页面")
//    public String edit(@PathVariable("id") Long id, ModelMap modelMap) throws Exception {
//        log.debug("跳转到编辑调度任务页面参数! id = {}", id);
//
//        ScheduleJob model = scheduleJobService.findById(id);
//
//        log.info("跳转到编辑调度任务信息页面成功!, id = {}", id);
//        modelMap.put("model", model);
//        return BASE_PATH + "job-edit";
//    }

    /**
     * 更新调度任务信息
     *
     * @param id
     * @param scheduleJob
     * @return
     */
    @OperationLog(value = "编辑调度任务")
    @PutMapping(value = "/{id}")
    @ApiOperation(value = "更新调度任务信息", notes = "更新调度任务信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseResult updateJob(HttpServletRequest request, @PathVariable("id") Long id,
                                    @RequestBody ScheduleJob scheduleJob, @ApiIgnore @CurrentUser User user) {

        //获取不进行xss过滤的request对象
        HttpServletRequest orgRequest = XssHttpServletRequestWrapper.getOrgRequest(request);


        log.debug("编辑调度任务参数! id= {}, scheduleJob = {}", id, scheduleJob);

        if (null == id) {
            return new ResponseResult(true, "ID不能为空！");
        }

        scheduleJob.setId(id);
        scheduleJob.setRemoteUrl(orgRequest.getParameter("remoteUrl"));
        scheduleJobService.updateScheduleJob(scheduleJob, user);

        log.info("编辑调度任务成功! id= {}, scheduleJob = {}", id, scheduleJob);
        return new ResponseResult(false, "编辑成功！");
    }


    /**
     * 根据主键ID删除调度任务
     *
     * @param id
     * @return
     */
    @OperationLog(value = "删除调度任务")
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "根据主键ID删除调度任务", notes = "根据主键ID删除调度任务")
    public ResponseResult delete(@PathVariable("id") Long id) {

        log.debug("删除调度任务! id = {}", id);

        if (null == id) {
            log.info("删除调度任务不存在! id = {}", id);
            return new ResponseResult(true, "调度任务不存在！");
        }

        scheduleJobService.deleteScheduleJob(id);

        log.info("删除调度任务成功! id = {}", id);
        return new ResponseResult(false, "删除成功!");
    }

    /**
     * 暂停调度任务
     *
     * @param id
     * @return
     */
    @OperationLog(value = "暂停调度任务")
    @PutMapping(value = "/pause/{id}")
    @ApiOperation(value = "暂停调度任务", notes = "暂停调度任务")
    public ResponseResult pauseJob(@PathVariable("id") Long id) {

        log.debug("暂停调度任务! id = {}", id);

        if (null == id) {
            log.info("暂停调度任务不存在! id = {}", id);
            return new ResponseResult(true, "暂停调度任务不存在!");
        }

        scheduleJobService.pauseJob(id);

        log.info("暂停调度任务成功! id = {}", id);
        return new ResponseResult(false, "暂停成功!");
    }


    @OperationLog(value = "暂停所有调度任务")
    @GetMapping(value = "/pauseAllJob")
    @ApiOperation(value = "暂停所有调度任务", notes = "暂停所有调度任务")
    public ResponseResult pauseAllJob() {
        log.debug("暂停所有调度任务");
        scheduleJobService.pauseAllJob();
        log.info("暂停所有调度任务成功");
        return new ResponseResult(false, "暂停所有调度任务成功!");
    }


    @OperationLog(value = "恢复所有调度任务")
    @GetMapping(value = "/resumAllJob")
    @ApiOperation(value = "恢复所有调度任务", notes = "恢复所有调度任务")
    public ResponseResult resumAllJob() {
        log.debug("恢复所有调度任务");
        scheduleJobService.resumAllJob();
        log.info("恢复所有调度任务成功");
        return new ResponseResult(false, "恢复所有调度任务成功!");
    }


    /**
     * 恢复调度任务
     *
     * @param id
     * @return
     */
    @OperationLog(value = "恢复调度任务")
    @PutMapping(value = "/resume/{id}")
    @ApiOperation(value = "恢复调度任务", notes = "恢复调度任务")
    public ResponseResult resumeJob(@PathVariable("id") Long id) {

        log.debug("恢复调度任务! id = {}", id);

        if (null == id) {
            log.info("恢复调度任务不存在! id = {}", id);
            return new ResponseResult(true, "恢复调度任务不存在!");
        }

        scheduleJobService.resumeJob(id);

        log.info("恢复调度任务成功! id = {}", id);
        return new ResponseResult(false, "恢复成功!");
    }

    /**
     * 运行一次调度任务
     *
     * @param id
     * @return
     */
    @OperationLog(value = "运行一次调度任务")
    @PutMapping(value = "/run/{id}")
    @ApiOperation(value = "运行一次调度任务", notes = "运行一次调度任务")
    public ResponseResult runOnce(@PathVariable("id") Long id) {

        log.debug("运行一次调度任务! id = {}", id);

        if (null == id) {
            log.info("运行一次调度任务不存在! id = {}", id);
            return new ResponseResult(true, "运行一次调度任务不存在!");
        }

        scheduleJobService.runOnce(id);

        log.info("运行一次调度任务成功! id = {}", id);
        return new ResponseResult(false, "运行成功!");
    }

    /**
     * 查看调度任务详情信息
     *
     * @param id
     * @param modelMap
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "查看调度任务详情信息", notes = "查看调度任务详情信息")
    public ResponseResult view(@PathVariable("id") Long id, ModelMap modelMap) {
        ScheduleJob model = scheduleJobService.findById(id);
        if (null != model) {
            //创建者
            User userCreate = userService.findById(model.getCreateBy());
            if (null != userCreate) {
                model.setCreateByName(userCreate.getUsername());
            }
            //修改者
            User userModify = userService.findById(model.getModifyBy());
            if (null != userModify) {
                model.setModifyByName(userModify.getUsername());
            }
        }
        modelMap.put("model", model);
        return new ResponseResult(false, "", modelMap);
    }

    /**
     * 检验任务是否存在
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/isExist")
    @ApiOperation(value = "检验任务是否存在", notes = "检验任务是否存在")
    public ResponseResult isExist(Long id, String jobName, String jobGroup) throws Exception {
        log.debug("检验任务是否存在参数! id= {}, jobName= {}, jobGroup= {}", id, jobName, jobGroup);
        if (StringUtils.isNotEmpty(jobName) && StringUtils.isNotEmpty(jobGroup)) {
            ScheduleJob record = scheduleJobService.findByJobNameAndJobGroup(jobName, jobGroup);
            if (null != record && !record.getId().equals(id)) {
                return new ResponseResult(false, "任务存在！");
            }
        }
        return new ResponseResult(false, "任务不存在！");
    }

}
