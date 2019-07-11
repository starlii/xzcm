package com.xzcmapi.model;

import com.xzcmapi.entity.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "新增活动参数实体")
public class SaveActivityModel {

    @ApiModelProperty(notes = "活动id（更新时传）")
    private Long activityId;

    @ApiModelProperty(notes = "活动名称/标题 -- 必填")
    private String activityName;

    @ApiModelProperty(notes = "活动备注1")
    private String activityRemark;

    @ApiModelProperty(notes = "活动备注2")
    private String activityRemarkSec;

    @ApiModelProperty(notes = "活动状态 -- 必填")
    private Integer activityStatus;

    @ApiModelProperty(notes = "是否开启 0--开启 1--关闭")
    private Integer approvalStatus;

    @ApiModelProperty(notes = "活动设置")
    private ActivitySettingModel activitySetting;

    @ApiModelProperty(notes = "活动规则设置")
    private RuleSettingModel ruleSetting;

    @ApiModelProperty(notes = "活动投票设置")
    private VoteSettingModel voteSetting;

    @ApiModelProperty(notes = "活动界面设置")
    private InterfaceSettingModel interfaceSetting;

    @ApiModelProperty(notes = "活动报名设置")
    private ApplySettingModel applySetting;

    @ApiModelProperty(notes = "活动礼物设置")
    private GiftSettingModel giftSetting;

    @ApiModelProperty(notes = "活动轮播图")
    private List<String> images;
}
