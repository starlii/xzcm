package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "选手搜索选项")
public class PlayerSearchModel{

    @ApiModelProperty(notes = "选手编号/姓名")
    private String search;

    @ApiModelProperty(notes = "活动id -- 必填",required = true)
    private Long activityId;

    @ApiModelProperty(notes = "0--审核通过，1--未通过")
    private Integer approvalStatus;

    @ApiModelProperty(notes = "page")
    private Integer page;

    @ApiModelProperty(notes = "size")
    private Integer size;

    @ApiModelProperty(notes = "sort")
    private String sort;

}
