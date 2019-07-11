package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "h5投票model")
public class H5VoteModel {

    @ApiModelProperty(notes = "选手id")
    private Long playerId;

//    @ApiModelProperty(notes = "微信名称")
//    private String name;

    @ApiModelProperty(notes = "微信openId")
    private String openId;
}
