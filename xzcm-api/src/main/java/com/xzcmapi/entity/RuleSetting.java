package com.xzcmapi.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Table(name = "xzcm_activity_rule")
public class RuleSetting extends BaseEntity {
    @Column(name = "is_follow")
    @ApiModelProperty(notes = "是否关注，0--不需要关注 1--投票需要关注 2--参加需要关注 3--参加投票都需要关注")
    private Integer isFollow;
    @Column(name = "approval_type")
    @ApiModelProperty(notes = "报名审核类型  0--人工审核 1--自动审核")
    private Integer approvalType;
    @Column(name = "min_players")
    @ApiModelProperty(notes = "最小人数")
    private Integer minPlayers;
    @Column(name = "user_limit")
    @ApiModelProperty(notes = "每人每日每用户投票数量限制")
    private Integer userLimit;
    @Column(name = "day_limit")
    @ApiModelProperty(notes = "每日没人")
    private Integer dayLimit;
    @Column(name = "sum_limit")
    @ApiModelProperty(notes = "没人最多")
    private Integer sumLimit;
    @Column(name = "vote_interval")
    @ApiModelProperty(notes = "投票时间间隔")
    private Integer voteInterval;
    @Column(name = "gift_limit")
    @ApiModelProperty(notes = "没人最多送礼物")
    private BigDecimal giftLimit;
    @Column(name = "is_remainder")
    @ApiModelProperty(notes = "投票消息提醒")
    private Integer isRemainder;
    @Column(name = "anonymous_gift")
    @ApiModelProperty(notes = "匿名送礼")
    private Integer anonymousGift;
    @Column(name = "vote_self")
    @ApiModelProperty(notes = "给自己投票")
    private Integer voteSelf;
    @Column(name = "vote_auth")
    @ApiModelProperty(notes = "投票验证")
    private Integer voteAuth;
    @Column(name = "auth_id")
    @ApiModelProperty(notes = "验证码id")
    private String authId;
    @Column(name = "auth_key")
    @ApiModelProperty(notes = "验证码key")
    private String authKey;
    @Column(name = "lock_condition")
    @ApiModelProperty(notes = "自动锁定的时间限制")
    private Integer lockCondition;
    @Column(name = "local_votes")
    @ApiModelProperty(notes = "自动锁定的票数限制")
    private Integer localVotes;
    @Column(name = "lock_time")
    @ApiModelProperty(notes = "自动锁定时常")
    private Integer lockTime;
    @Column(name = "limit_area")
    @ApiModelProperty(notes = "地区限制")
    private String limitArea;
    @Column(name = "tencent_key")
    @ApiModelProperty(notes = "腾讯地图API接口的KEY")
    private String tencentKey;
    @Column(name = "activity_id")
    @ApiModelProperty(notes = "外键：保存时不需要传")
    private Long activityId;

}
