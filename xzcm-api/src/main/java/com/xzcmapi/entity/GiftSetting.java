package com.xzcmapi.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Table(name = "xzcm_gift_setting")
public class GiftSetting extends BaseEntity{
    @Column(name = "realtime_show")
    @ApiModelProperty(notes = "活动页面实时弹出送礼物记录 0--弹 1--不弹")
    private Integer realtimeShow;
    @Column(name = "gift_page")
    @ApiModelProperty(notes = "送礼物界面 0--1钻石界面 1--界面自定义 2--自定义礼物界面")
    private Integer giftPage;
    @Column(name = "gift_unit")
    @ApiModelProperty(notes = "比例单位")
    private Integer giftUnit;
    @Column(name = "gift_name")
    @ApiModelProperty(notes = "礼物名称")
    private String giftName;
    @Column(name = "gift_price")
    @ApiModelProperty(notes = "礼物价格")
    private BigDecimal giftPrice;
    @Column(name = "gift_votes")
    @ApiModelProperty(notes = "礼物票数")
    private Integer giftVotes;
    @Column(name = "gift_image")
    @ApiModelProperty(notes = "礼物图片")
    private String giftImage;

    @Column(name = "activity_id")
    @ApiModelProperty(notes = "外键：保存时不需要传")
    private Long activityId;
}
