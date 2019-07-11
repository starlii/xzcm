package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

@Data
@ApiModel(description = "h5选手详情页")
public class H5PlayerDetailModel {

    @ApiModelProperty(notes = "选手id")
    private Long playerId;

    @ApiModelProperty(notes = "选手编号")
    private Long num;

    @ApiModelProperty(notes = "选手姓名")
    private String name;

    @ApiModelProperty(notes = "选手介绍")
    private String playerDesc;

    @ApiModelProperty(notes = "选手比赛宣言")
    private String declaration;

    @ApiModelProperty(notes = "当前票数")
    private Integer currentVotes;

    @ApiModelProperty(notes = "距离上一名")
    private Integer gapPrevious;

    @ApiModelProperty(notes = "名次")
    private Integer rank;

    @ApiModelProperty(notes = "锁定状态")
    private Integer lockStatus;

    @ApiModelProperty(notes = "审核状态")
    private Integer approvalStatus;

    @ApiModelProperty(notes = "投票状态")
    private Integer voteStatus;

    @ApiModelProperty(notes = "选手图片")
    private List<String> images;
}
