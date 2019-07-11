package com.xzcmapi.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Cleanup;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(description = "选手实体")
@Table(name = "xzcm_activity_player")
public class Player extends BaseEntity{

    @Column(name = "name")
    @ApiModelProperty(notes = "选手姓名")
    private String name;
    @ApiModelProperty(notes = "选手电话")
    @Column(name = "phone")
    private String phone;
    @Column(name = "declaration")
    @ApiModelProperty(notes = "选手比赛宣言")
    private String declaration;
    @Column(name = "actual_votes")
    @ApiModelProperty(notes = "实际票数")
    private Integer actualVotes;
    @Column(name = "manual_votes")
    @ApiModelProperty(notes = "手动票数")
    private Integer manualVotes;
    @Column(name = "gift")
    @ApiModelProperty(notes = "礼物")
    private BigDecimal gift;
    @Column(name = "player_desc")
    @ApiModelProperty(notes = "备注")
    private String playerDesc;
    @Column(name = "image")
    @ApiModelProperty(notes = "图片地址")
    private String image;

    @Column(name = "fast_image")
    @ApiModelProperty(notes = "FastDfs图片地址")
    private String fastImage;


    @ApiModelProperty(notes = "活动id -- 必传",required = true)
    @Column(name = "activity_id")
    private Long activityId;
    @ApiModelProperty(notes = "浏览量")
    @Column(name = "views")
    private Integer views;
    @ApiModelProperty(notes = "分享量")
    @Column(name = "shares")
    private Integer shares;

    @ApiModelProperty(notes = "ip量")
    @Column(name = "ip_amount")
    private Integer ipAmount;

    @ApiModelProperty(notes = "备注")
    @Column(name = "remark")
    private String remark;

    @ApiModelProperty(notes = "锁定状态")
    @Column(name = "lock_status")
    private Integer lockStatus;

    @ApiModelProperty(notes = "审核状态")
    @Column(name = "approval_status")
    private Integer approvalStatus;

    @ApiModelProperty(notes = "投票状态")
    @Column(name = "vote_status")
    private Integer voteStatus;

    @ApiModelProperty(notes = "锁定时间")
    @Column(name = "lock_time")
    private Integer lockTime;

    @ApiModelProperty(notes = "更新时间")
    @Column(name = "update_time")
    private Date updateTime;

    @ApiModelProperty(notes = "是否今日之星")
    @Column(name = "star")
    private Integer star;

    @ApiModelProperty(notes = "定时是否开启 0--开启 1--未开启")
    @Column(name = "schedul_status")
    private Integer schedulStatus;

    @ApiModelProperty(notes = "定时任务id")
    @Column(name = "schedul_id")
    private Long schedulId;

    @ApiModelProperty(notes = "选手编号")
    @Column(name = "num")
    private Long num;

    @Column(name = "open_id")
    private String openId;

    @Column(name = "lock_date")
    private Date lockDate;
}
