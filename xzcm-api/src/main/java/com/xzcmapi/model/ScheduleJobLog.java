package com.xzcmapi.model;

import com.xzcmapi.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ApiModel
@EqualsAndHashCode(callSuper = false)
public class ScheduleJobLog extends BaseEntity {

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID",notes = "任务ID")
    private Long jobId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称",notes = "任务名称")
    private String jobName;

    /**
     * 任务分组
     */
    @ApiModelProperty(value = "任务分组",notes = "任务分组")
    private String jobGroup;

    /**
     * 执行计划
     */
    @ApiModelProperty(value = "执行计划",notes = "执行计划")
    private String cron;

    /**
     * 调度方式 false 远程 true 本地
     */
    @ApiModelProperty(value = "调度方式 false 远程 true本地",notes = "调度方式 false 远程 true本地")
    private Boolean isLocal;

    /**
     * 远程请求方式 只支持POST
     */
    @ApiModelProperty(value = "远程请求方式 只支持POST",notes = "远程请求方式 只支持POST")
    private String remoteRequestMethod;

    /**
     * 远程执行url
     */
    @ApiModelProperty(value = "远程执行URL",notes = "远程执行URL")
    private String remoteUrl;

    /**
     * 任务执行时调用哪个类的方法 包名+类名
     */
    @ApiModelProperty(value = "任务执行时调用哪个类的方法 包名+类名",notes = "任务执行时调用哪个类的方法 包名+类名")
    private String beanClass;

    /**
     * 任务调用的方法名
     */
    @ApiModelProperty(value = "任务调用的方法名",notes = "任务调用的方法名")
    private String methodName;

    /**
     * 参数
     */
    @ApiModelProperty(value = "参数",notes = "参数")
    private String params;

    /**
     * 是否异步  0否 1是
     */
    @ApiModelProperty(value = "是否异步  0否  1是",notes = "是否异步  0否  1是")
    private Boolean isAsync;

    /**
     * 执行状态 0失败 1成功
     */
    @ApiModelProperty(value = "执行状态 0失败 1成功",notes = "执行状态 0失败 1成功")
    private Integer status;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述",notes = "描述")
    private String remarks;

    /**
     * 失败信息
     */
    @ApiModelProperty(value = "失败信息",notes = "失败信息")
    private String error;

    /**
     * 耗时(单位：毫秒)
     */
    @ApiModelProperty(value = "耗时(单位：毫秒)",notes = "耗时(单位：毫秒)")
    private Long times;
}
