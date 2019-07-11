package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
@ApiModel(description = "活动设置参数")
public class ActivitySettingModel {

    @ApiModelProperty(notes = "报名开始时间")
    private Date applyStartTime;

    @ApiModelProperty(notes = "报名结束时间")
    private Date applyEndTime;

    @ApiModelProperty(notes = "投票开始时间")
    private Date voteStartTime;

    @ApiModelProperty(notes = "投票结束时间")
    private Date voteEndTime;

    @ApiModelProperty(notes = "投票结束后可送礼物时间")
    private Integer endGiftTime;

    @ApiModelProperty(notes = "模式一  1--礼物模式   2--普通投票模式  默认1")
    private Integer mode1;

    @ApiModelProperty(notes = "模式二  1--排行榜显示礼物点数 2--排行榜不显示礼物点数  默认2")
    private Integer mode2;

    @ApiModelProperty(notes = "模式三 1--选手页显示礼物点数 2--选手页不显示礼物点数 默认2")
    private Integer mode3;

    @ApiModelProperty(notes = "活动结束后不显示页面")
    private Integer isShowPage;
}
