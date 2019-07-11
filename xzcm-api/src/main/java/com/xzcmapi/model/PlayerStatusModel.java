package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@ApiModel(description = "修改选手状态model")
@Data
public class PlayerStatusModel {

    private Long playerId;

    @ApiModelProperty(notes = "锁定状态 0--是 1-- 否")
    private Integer lockStatus;

    @ApiModelProperty(notes = "审核状态0--是 1--否")
    private Integer approvalStatus;

    @ApiModelProperty(notes = "投票状态0--是 1--否")
    private Integer voteStatus;

    @ApiModelProperty(notes = "锁定时间，修改锁定状态为锁定时需要指定锁定时间")
    private Integer lockTime;
}
