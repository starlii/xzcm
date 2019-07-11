package com.xzcmapi.model;

import com.xzcmapi.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(description = "礼物设置参数")
public class GiftSettingModel{

    @ApiModelProperty(notes = "活动页面实时弹出送礼物记录 0--弹 1--不弹")
    private Integer realtimeShow;

    @ApiModelProperty(notes = "送礼物界面 0--1钻石界面 1--界面自定义 2--自定义礼物界面")
    private Integer giftPage;

    @ApiModelProperty(notes = "比例单位")
    private Integer giftUnit;

    @ApiModelProperty(notes = "礼物价格，多个礼物，list")
    List<BigDecimal> giftPrice;

}
