package com.xzcmapi.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
@ApiModel(description = "规则设置参数实体")
public class RuleSettingModel {

    @ApiModelProperty(notes = "是否关注，默认是0   0--不需要关注 1--投票需要关注 2--参加需要关注 3--参加投票都需要关注")
    private Integer isFollow;

    @ApiModelProperty(notes = "报名审核类型  默认是0 0--人工审核 1--自动审核")
    private Integer approvalType;

    @ApiModelProperty(notes = "最小人数")
    private Integer minPlayers;

    @ApiModelProperty(notes = "每人每日每用户投票数量限制")
    private Integer userLimit;

    @ApiModelProperty(notes = "每日没人")
    private Integer dayLimit;

    @ApiModelProperty(notes = "没人最多")
    private Integer sumLimit;

    @ApiModelProperty(notes = "投票时间间隔")
    private Integer voteInterval;

    @ApiModelProperty(notes = "没人最多送礼物")
    private BigDecimal giftLimit;

    @ApiModelProperty(notes = "投票消息提醒 0--是 1--否   默认是1")
    private Integer isRemainder;

    @ApiModelProperty(notes = "匿名送礼 0--是  1--否 默认1")
    private Integer anonymousGift;

    @ApiModelProperty(notes = "给自己投票 0--是 1--否 默认0")
    private Integer voteSelf;

    @ApiModelProperty(notes = "投票验证 0--是 1--否 默认否")
    private Integer voteAuth;

    @ApiModelProperty(notes = "验证码id")
    private String authId;

    @ApiModelProperty(notes = "验证码key")
    private String authKey;

    @ApiModelProperty(notes = "自动锁定的时间限制")
    private Integer lockCondition;

    @ApiModelProperty(notes = "自动锁定的票数限制")
    private Integer localVotes;

    @ApiModelProperty(notes = "自动锁定时常")
    private Integer lockTime;

    @ApiModelProperty(notes = "地区限制")
    private String limitArea;

    @ApiModelProperty(notes = "腾讯地图API接口的KEY")
    private String tencentKey;
}
