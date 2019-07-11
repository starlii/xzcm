package com.xzcmapi.model;

import com.xzcmapi.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@ApiModel(description = "投票设置参数")
public class VoteSettingModel{

    @ApiModelProperty(notes = "轮播图跳转链接")
    private String forwardLink;

    @ApiModelProperty(notes = "每日之星功能 0--开启 1--关闭 默认1")
    private Integer isStar;

    @ApiModelProperty(notes = "投票成功跳转后的广告图片链接")
    private String successImage;

    @ApiModelProperty(notes = "点击弹出广告跳转到买礼物页面 0--跳转 1--不跳转 默认1")
    private Integer isGiftFor;

    @ApiModelProperty(notes = "投票成功后跳转 0--跳转 1--不跳转 默认0")
    private Integer isVoteFor;

    @ApiModelProperty(notes = "活动规则")
    private String activityRule;

    @ApiModelProperty(notes = "奖品介绍")
    private String giftDes;

}
