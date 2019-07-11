package com.xzcmapi.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Table(name = "xzcm_report_form")
public class ReportForm extends BaseEntity {


    @Column(name = "activitys")
    @ApiModelProperty(notes = "活动数")
    private Integer activitys;

    @Column(name = "transactions")
    @ApiModelProperty(notes = "交易数")
    private Integer transactions;

    @Column(name = "amount")
    @ApiModelProperty(notes = "金额")
    private BigDecimal amount;

    @Column(name = "user_id")
    @ApiModelProperty(notes = "用户id")
    private Long userId;

    @Column(name = "status")
    @ApiModelProperty(notes = "状态  0- 支付   1-未支付")
    private Integer status;

    @Column(name = "type")
    private Integer type;
}
