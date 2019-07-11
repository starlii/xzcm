package com.xzcmapi.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BatchPlayerModel {

    private Long playerId;
    @ApiModelProperty(notes = "票数")
    private Integer votes;
    @ApiModelProperty(notes = "礼物")
    private BigDecimal gift;
    @ApiModelProperty(notes = "浏览量")
    private Integer views;
}
