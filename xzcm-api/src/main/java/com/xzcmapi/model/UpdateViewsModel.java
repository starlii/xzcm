package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@ApiModel(description = "updateviews")
public class UpdateViewsModel {

    @ApiModelProperty("活动Id")
    private Long activityId;

    @ApiModelProperty("浏览量")
    private Integer views;
}
