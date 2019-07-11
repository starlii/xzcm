package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SearchModel {

    @ApiModelProperty(notes = "名称或者备注")
     private String search;

    @ApiModelProperty(notes = "时间搜索标识1...7 为当前时间向后七天")
     private int time;

    @ApiModelProperty(notes = "状态")
    private Integer status;

    @ApiModelProperty(notes = "page")
    private Integer page;

    @ApiModelProperty(notes = "size")
    private Integer size;

    @ApiModelProperty(notes = "sort")
    private String sort;
}
