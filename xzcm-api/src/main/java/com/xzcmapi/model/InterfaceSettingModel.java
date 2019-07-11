package com.xzcmapi.model;

import com.xzcmapi.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(description = "页面设置参数")
public class InterfaceSettingModel {

    @ApiModelProperty(notes = "首页显示顺序 默认4 0--按最新倒叙 1--按最新顺序 2--按编号倒叙 3--按编号顺序 4--按票数倒叙 5--按票数顺序 6--按礼物倒叙 7--按礼物顺序 8--按最新投票倒叙 9--按最新投票顺序")
    private Integer homeShowOrder;

    @ApiModelProperty(notes = "虚拟浏览量")
    private Integer visualViews;

    @ApiModelProperty(notes = "排行榜数量")
    private Integer leaderboardNum;

    @ApiModelProperty(notes = "礼物详情 0--展示 1--不展示 默认不展示")
    private Integer showGift;

    @ApiModelProperty(notes = "网络人气 0--显示 1--不显示 默认不展示")
    private Integer showPopularity;

    @ApiModelProperty(notes = "生成海报 0--生成 1--不生成")
    private Integer isPoster;

    @ApiModelProperty(notes = "未关注引导提示")
    private String followTips;

    @ApiModelProperty(notes = "滚动公告")
    private String announcement;

    @ApiModelProperty(notes = "首页背景音乐链接")
    private String music;

    @ApiModelProperty(notes = "自定义颜色页面code")
    private String customizeColor;

    @ApiModelProperty(notes = "主题颜色code")
    private String themeColor;

    @ApiModelProperty(notes = "首页边框特效 至少购买金额")
    private BigDecimal specialAmount;

    @ApiModelProperty(notes = "首页边框特效 时常")
    private Date specialTime;

    @ApiModelProperty(notes = "边框特效图片url")
    private String specialImageUrl;

    @Column(name = "home_special_image")
    @ApiModelProperty(notes = "首页漂浮物特效 url")
    private String homeSpecialImage;

    @ApiModelProperty(notes = "详情页广告图片url")
    private String detailImage;

    @ApiModelProperty(notes = "详情页跳转链接")
    private String detailUrl;

}