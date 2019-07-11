package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "删除model")
@Data
public class DeleteFileModel {
    private Long activityId;
    private Long playerId;
}
