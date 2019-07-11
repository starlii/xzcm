package com.xzcmapi.model;

import com.sun.xml.internal.rngom.digested.DTextPattern;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "报表搜索项")
public class ReportSearchModel {

    @ApiModelProperty(notes = "时间标识  0-昨天 1-本周 2-上周 3-本月 4-上月  （与开始是时间和结束时间只传一个）")
    private Integer timeFlag;

    @ApiModelProperty(notes = "开始时间")
    private Date startTime;

    @ApiModelProperty(notes = "结束时间")
    private Date endTime;

    @ApiModelProperty(notes = "用户名")
    private String username;

    private Integer page;

    private Integer size;
}
