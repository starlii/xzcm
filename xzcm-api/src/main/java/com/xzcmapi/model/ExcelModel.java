package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(description = "导出数据model")
public class ExcelModel {
    @ApiModelProperty(notes = "选手id/编号")
    private Long playerId;

    @ApiModelProperty(notes = "选手姓名")
    private String name;

    @ApiModelProperty(notes = "选手电话")
    private String phone;

    @ApiModelProperty(notes = "票数")
    private Integer votes;

    @ApiModelProperty(notes = "礼物")
    private BigDecimal gift;

    @ApiModelProperty(notes = "报名时间")
    private Date createTime;

    @ApiModelProperty(notes = "名次")
    private Integer rank;

}
