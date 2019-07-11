package com.xzcmapi.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "活动、选手分享成功后的回调model")
public class ShareCallbackModel {

    @ApiModelProperty(notes = "活动id")
    private Long activityId;

    @ApiModelProperty(notes = "选手id")
    private Long playerId;
}
