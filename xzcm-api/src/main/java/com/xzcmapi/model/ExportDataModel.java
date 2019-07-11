package com.xzcmapi.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "导出参数")
public class ExportDataModel {

    @ApiModelProperty(notes = "活动id")
    private Long activityId;

    @ApiModelProperty(notes = "选手id/编号")
    private List<Long> playerIds;
}
