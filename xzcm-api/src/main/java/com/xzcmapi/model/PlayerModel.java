package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "选手对象")
public class PlayerModel {

    @ApiModelProperty(notes = "选手id/编号")
    private Long playerId;

    @ApiModelProperty(notes = "选手姓名")
    private String name;

    @ApiModelProperty(notes = "选手电话")
    private String phone;

    @ApiModelProperty(notes = "选手比赛宣言")
    private String declaration;

    @ApiModelProperty(notes = "票数")
    private Integer votes;

    @ApiModelProperty(notes = "实际票数")
    private Integer actualVotes;

    @ApiModelProperty(notes = "礼物")
    private BigDecimal gift;

    @ApiModelProperty(notes = "描述")
    private String playerDesc;


    @ApiModelProperty(notes = "图片地址")
    private String image;

    @ApiModelProperty(notes = "排名")
    private Integer rank;

    @ApiModelProperty(notes = "参赛时间")
    private Date createTime;

    @ApiModelProperty(notes = "浏览量")
    private Integer views;

    @ApiModelProperty(notes = "分享量")
    private Integer shares;

    @ApiModelProperty(notes = "ip量")
    private Integer ipAmount;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "锁定状态 0--是 1-- 否")
    private Integer lockStatus;

    @ApiModelProperty(notes = "审核状态0--是 1--否")
    private Integer approvalStatus;

    @ApiModelProperty(notes = "投票状态0--是 1--否")
    private Integer voteStatus;

    @ApiModelProperty(notes = "是否今日之星 0--是 1--否")
    private Integer star;

    @ApiModelProperty(notes = "定时是否开启 0--开启 1--未开启")
    private Integer schedulStatus;

    @ApiModelProperty(notes = "选手图片集合")
    private List<String> images;

    @ApiModelProperty(notes = "选手编号")
    private Long num;
    
    @ApiModelProperty(notes = "活动名称")
    private String activityName;
}
