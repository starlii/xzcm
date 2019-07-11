package com.xzcmapi.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(description = "充值报表")
public class ChargeModel {

    @ApiModelProperty(notes = "id")
    private Long id;

    @ApiModelProperty(notes = "生成时间")
    private Date createTime;

    @ApiModelProperty(notes = "交易数")
    private Integer transactions;

    @ApiModelProperty(notes = "金额")
    private BigDecimal amount;

    @ApiModelProperty(notes = "用户名")
    private String username;

}
