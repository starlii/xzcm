package com.xzcmapi.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Table(name = "xzcm_system_log")
public class Log extends BaseEntity{
    /**
     * 操作者姓名
     */
    @ApiModelProperty(value = "应用",notes = "应用")
    @Column(name = "app_name")
    private String appName;
    /**
     * 用户操作
     */
    @ApiModelProperty(value = "操作类型",notes = "操作类型")
    @Column(name = "log_type")
    private Integer logType;
    /**
     * 访问者ip
     */

    @ApiModelProperty(value = "操作人姓名",notes = "操作人姓名")
    @Column(name = "username")
    private String username;

    @ApiModelProperty(value = "操作",notes = "操作")
    @Column(name = "operation")
    private String operation;

    @ApiModelProperty(value = "方法名称",notes = "方法名称")
    @Column(name = "method_name")
    private String methodName;

    @ApiModelProperty(value = "请求方法",notes = "请求方法")
    @Column(name = "request_method")
    private String requestMethod;

    @ApiModelProperty(value = "请求参数",notes = "请求参数")
    @Column(name = "request_params")
    private String params;

    @ApiModelProperty(value = "异常code",notes = "异常code")
    @Column(name = "exception_code")
    private String exceptionCode;

    @ApiModelProperty(value = "异常信息",notes = "异常信息")
    @Column(name = "exception_detail")
    private String exceptionDetail;

    @ApiModelProperty(value = "请求耗时",notes = "请求耗时")
    @Column(name = "time_consuming")
    private Long timeConsuming;

    @ApiModelProperty(value = "访问者ip",notes = "访问者ip")
    @Column(name = "request_ip")
    private String requestIp;
    /**
     * 请求uri
     */
    @ApiModelProperty(value = "请求uri",notes = "请求uri")
    @Column(name = "request_uri")
    private String requestUri;
    /**
     * 客户端信息
     */
    @ApiModelProperty(value = "客户端信息",notes = "客户端信息")
    @Column(name = "user_agent")
    private String userAgent;


    public enum OperateType{
        OPERATE_TYPE_ADD("operate_type_add","增加"),OPERATE_TYPE_UPDATE("operate_type_update","修改"),
        OPERATE_TYPE_DELETE("operate_type_delete","删除"),
        OPERATE_TYPE_LOGIN("operate_type_login","登录"),OPERATE_TYPE_EXIT("operate_type_exit","登出");
        private String value;
        private String remark;

        OperateType(String value, String remark) {
            this.value = value;
            this.remark = remark;
        }

        public String getValue() {
            return value;
        }

        public String getRemark() {
            return remark;
        }
    }
}