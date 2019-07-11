package com.xzcmapi.model;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
public class ActivityDateModel {

    private Long activityId;
    private Date activetyStartTime;
    private Date activityEndTime;
    private Date voteStartTime;
    private Date voteEndTime;
    private Integer approvalStatus;
}
