package com.xzcmapi.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "xzcm_vote_setting")
public class VoteSetting extends BaseEntity {

    @Column(name = "forward_link")
    @ApiModelProperty(notes = "轮播图跳转链接")
    private String forwardLink;
    @Column(name = "is_star")
    @ApiModelProperty(notes = "每日之星功能 0--开启 1--关闭")
    private Integer isStar;
    @Column(name = "success_image")
    @ApiModelProperty(notes = "投票成功跳转后的广告图片链接")
    private String successImage;
    @Column(name = "is_gift_for")
    @ApiModelProperty(notes = "点击弹出广告跳转到买礼物页面 0--跳转 1--不跳转")
    private Integer isGiftFor;
    @Column(name = "is_vote_for")
    @ApiModelProperty(notes = "投票成功后跳转 0--跳转 1--不跳转")
    private Integer isVoteFor;
    @Column(name = "activity_rule")
    @ApiModelProperty(notes = "活动规则")
    private String activityRule;
    @Column(name = "gift_des")
    @ApiModelProperty(notes = "奖品介绍")
    private String giftDes;

    @Column(name = "activity_id")
    @ApiModelProperty(notes = "外键：保存时不需要传")
    private Long activityId;
}
