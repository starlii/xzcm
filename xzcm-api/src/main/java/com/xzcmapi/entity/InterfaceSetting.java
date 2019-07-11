package com.xzcmapi.entity;

import com.sun.xml.internal.rngom.parse.host.Base;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "xzcm_interface_setting")
public class InterfaceSetting extends BaseEntity {
    @Column(name = "home_show_order")
    @ApiModelProperty(notes = "首页显示顺序 0--按最新倒叙 1--按最新顺序 2--按编号倒叙 3--按编号顺序 4--按票数倒叙 5--按票数顺序 6--按礼物倒叙 7--按礼物顺序 8--按最新投票倒叙 9--按最新投票顺序")
    private Integer homeShowOrder;
    @Column(name = "visual_views")
    @ApiModelProperty(notes = "虚拟浏览量")
    private Integer visualViews;
    @Column(name = "leaderboard_num")
    @ApiModelProperty(notes = "排行榜数量")
    private Integer leaderboardNum;
    @Column(name = "show_gift")
    @ApiModelProperty(notes = "礼物详情 0--展示 1--不展示")
    private Integer showGift;
    @Column(name = "show_popularity")
    @ApiModelProperty(notes = "网络人气 0--显示 1--不显示")
    private Integer showPopularity;
    @Column(name = "is_poster")
    @ApiModelProperty(notes = "生成海报 0--生成 1--不生成")
    private Integer isPoster;
    @Column(name = "follow_tips")
    @ApiModelProperty(notes = "未关注引导提示")
    private String followTips;
    @Column(name = "announcement")
    @ApiModelProperty(notes = "滚动公告")
    private String announcement;
    @Column(name = "music")
    @ApiModelProperty(notes = "首页背景音乐链接")
    private String music;
    @Column(name = "customize_color")
    @ApiModelProperty(notes = "自定义颜色页面code")
    private String customizeColor;
    @Column(name = "theme_color")
    @ApiModelProperty(notes = "主题颜色code")
    private String themeColor;
    @Column(name = "special_amount")
    @ApiModelProperty(notes = "首页边框特效 至少购买金额")
    private BigDecimal specialAmount;
    @Column(name = "special_time")
    @ApiModelProperty(notes = "首页边框特效 时常")
    private Date specialTime;
    @Column(name = "special_image_url")
    @ApiModelProperty(notes = "边框特效图片url")
    private String specialImageUrl;
    @Column(name = "home_special_image")
    @ApiModelProperty(notes = "首页漂浮物特效 url")
    private String homeSpecialImage;
    @Column(name = "detail_image")
    @ApiModelProperty(notes = "详情页广告图片url")
    private String detailImage;
    @Column(name = "detail_url")
    @ApiModelProperty(notes = "详情页跳转链接")
    private String detailUrl;

    @Column(name = "activity_id")
    @ApiModelProperty(notes = "外键：保存时不需要传")
    private Long activityId;
}