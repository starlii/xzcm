package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@ApiModel(description = "微活动对象")
public class ActivityModel {

    @ApiModelProperty(notes = "主键id")
    private Long activityId;

    @ApiModelProperty(notes = "活动图片")
    private String activityImage;

    @ApiModelProperty(notes = "活动名称")
    private String activityName;

    @ApiModelProperty(notes = "活动开始时间")
    private Date activityStartTime;

    @ApiModelProperty(notes = "活动结束时间")
    private Date activityEndTime;

    @ApiModelProperty(notes = "选手数量")
    private Integer activityPlayers;

    @ApiModelProperty(notes = "金额")
    private BigDecimal activityAmount;

    @ApiModelProperty(notes = "总浏览量")
    private Integer activityViews;

    @ApiModelProperty(notes = "活动链接")
    private String activityUrl;

    @ApiModelProperty(notes = "活动状态 0--待审核 1--报名中 2--进行中 3--已结束 4--今日开始 5--今日结束 6--未开始")
    private Integer activityStatus;

    @ApiModelProperty(notes = "状态名称")
    private String statusName;

    @ApiModelProperty(notes = "活动备注1")
    private String activityRemark;

    @ApiModelProperty(notes = "活动备注2")
    private String activityRemarkSec;

    @ApiModelProperty(notes = "创建时间")
    private Date createTime;

    @ApiModelProperty(notes = "实际票数")
    private Integer actVotes;

    @ApiModelProperty(notes = "后台票数")
    private Integer mualVotes;

    @ApiModelProperty(notes = "总票数")
    private Integer votes;

    @ApiModelProperty(notes = "实际浏览量")
    private Integer actViews;

    @ApiModelProperty(notes = "后台浏览量")
    private Integer mualViews;

    @ApiModelProperty(notes = "分享量")
    private Integer activityShares;



    public static String getStatusName(Integer status){
        Map<Integer,String> map = new HashMap<>();
        map.put(0,"关闭");
        map.put(1,"报名中");
        map.put(2,"进行中");
        map.put(3,"已结束");
        map.put(4,"今日开始");
        map.put(5,"今日结束");
        map.put(6,"未开始");
        return map.get(status);
    }

}
