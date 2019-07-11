package com.xzcmapi.model;


import com.xzcmapi.entity.Player;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "h5首页model")
public class H5HomePageModel {

    @ApiModelProperty(notes = "活动id")
    private Long activitId;

    @ApiModelProperty(notes = "今日之星id")
    private Long starPlayer;

    @ApiModelProperty(notes = "主题颜色")
    private String themeColor;

    @ApiModelProperty(notes = "首页轮播信息")
    private String homeMessage = "活动开启后，每天上午9点系统自动更新当天排名第一者，免费赠送价值1000元投票首页广告推广一天，当选今日之星，将更加强有力的提高曝光率和知名度！";

    @ApiModelProperty(notes = "轮播图片（包括今日之星）")
    private List<String> images;

    @ApiModelProperty(notes = "参赛选手数量")
    private Integer playerNum;

    @ApiModelProperty(notes = "活动总投票数量")
    private Integer votes;

    @ApiModelProperty(notes = "总浏览量")
    private Integer views;

    @ApiModelProperty(notes = "投票开始时间")
    private Date voteStartTime;

    @ApiModelProperty(notes = "投票结束时间")
    private Date voteEndTime;

    @ApiModelProperty(notes = "活动开始时间")
    private Date activityStartTime;

    @ApiModelProperty(notes = "活动结束时间")
    private Date activityEndTime;

    @ApiModelProperty(notes = "人气选手")
    private List<H5PlayerDetailModel> popularityPlayers;

    @ApiModelProperty(notes = "最新参与选手")
    private List<H5PlayerDetailModel> latestPlayers;

    @ApiModelProperty(notes = "今日之星选手")
    private H5PlayerDetailModel todayPlayer;

}
