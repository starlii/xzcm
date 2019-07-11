package com.xzcmapi.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "xzcm_activity_setting")
public class ActivitySetting extends BaseEntity{

    @Column(name = "apply_start_time")
    @ApiModelProperty(notes = "报名开始时间")
    private Date applyStartTime;
    @Column(name = "apply_end_time")
    @ApiModelProperty(notes = "报名结束时间")
    private Date applyEndTime;
    @Column(name = "vote_start_time")
    @ApiModelProperty(notes = "投票开始时间")
    private Date voteStartTime;
    @Column(name = "vote_end_time")
    @ApiModelProperty(notes = "投票结束时间")
    private Date voteEndTime;
    @Column(name = "end_gift_time")
    @ApiModelProperty(notes = "投票结束后可送礼物时间")
    private Integer endGiftTime;
    @Column(name = "is_show_page")
    @ApiModelProperty(notes = "活动结束后不显示页面")
    private Integer isShowPage;

    @Column(name = "activity_id")
    @ApiModelProperty(notes = "外键：保存时不需要传")
    private Long activityId;

    @Column(name = "mode1")
    @ApiModelProperty(notes = "模式一  1--礼物模式   2--普通投票模式  默认1")
    private Integer mode1;

    @Column(name = "mode2")
    @ApiModelProperty(notes = "模式二  1--排行榜显示礼物点数 2--排行榜不显示礼物点数  默认2")
    private Integer mode2;

    @Column(name = "mode3")
    @ApiModelProperty(notes = "模式三 1--选手页显示礼物点数 2--选手页不显示礼物点数 默认2")
    private Integer mode3;
}
