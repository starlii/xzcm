package com.xzcmapi.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "定时投票model")
@Data
public class PlayerScheduledModel {

    @ApiModelProperty(notes = "选手id")
    private Long playerId;

    @ApiModelProperty(notes = "设置隔多少秒投票")
    private Integer seconds;

    @ApiModelProperty(notes = "设置投多少票")
    private Integer votes;
}
