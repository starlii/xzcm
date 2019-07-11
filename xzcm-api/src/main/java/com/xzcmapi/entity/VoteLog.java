package com.xzcmapi.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@Table(name = "xzcm_vote_log")
@ApiModel(description = "投票日志")
public class VoteLog extends BaseEntity {

    @Column(name = "name")
    @ApiModelProperty(notes = "投票人id")
    private String name;

    @Column(name = "open_id")
    @ApiModelProperty(notes = "微信openId")
    private String openId;

    @Column(name = "remark")
    @ApiModelProperty(notes = "备注")
    private String remark;

    @Column(name = "ip")
    @ApiModelProperty(notes = "ip地址")
    private String ip;

    @Column(name = "player_id")
    @ApiModelProperty(notes = "关联选手id")
    private Long playerId;

    @Transient
    @ApiModelProperty(notes = "选手姓名")
    private String playerName;

    @Transient
    private String activityName;

    @Transient
    private Long activityId;
}
