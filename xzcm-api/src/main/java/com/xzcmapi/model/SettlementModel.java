package com.xzcmapi.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(description = "结算报表")
public class SettlementModel {

    @ApiModelProperty(notes = "id")
    private Long id;

    @ApiModelProperty(notes = "活动数")
    private Integer activitys;

    @ApiModelProperty(notes = "金额")
    private BigDecimal amount;

    @ApiModelProperty(notes = "生成时间")
    private Date createTime;

    @ApiModelProperty(notes = "用户名")
    private String username;

    @ApiModelProperty(notes = "状态  0- 支付   1-未支付")
    private Integer status;

    @ApiModelProperty(notes = "代理系数")
    private Double agentValue;

    @ApiModelProperty(notes = "代理金额")
    private BigDecimal agentAmount;
}
