package com.xzcmapi.model;

import com.xzcmapi.entity.GiftLog;
import com.xzcmapi.entity.VoteLog;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(description = "今日交易模型")
public class TodayDealsModel {

    @ApiModelProperty(notes = "今日交易金額")
    private BigDecimal todayDeals;

    @ApiModelProperty(notes = "昨日交易金額")
    private BigDecimal yesterdayDeals;

    @ApiModelProperty(notes = "总交易额度")
    private BigDecimal totalDeals;

    @ApiModelProperty(notes = "进行中数量")
    private Integer processings;

    @ApiModelProperty(notes = "今日开始")
    private Integer todayStarts;

    @ApiModelProperty(notes = "今日结束")
    private Integer todayEnds;

    @ApiModelProperty(notes = "投票活动")
    private Integer activitys;

    @ApiModelProperty(notes = "未开始")
    private Integer notStarts;

    @ApiModelProperty(notes = "已结束")
    private Integer overs;

    @ApiModelProperty(notes = "最新礼物")
    private List<GiftLog> giftLogs;

    @ApiModelProperty(notes = "最新投票")
    private List<VoteLog> voteLogs;
}
