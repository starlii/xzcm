package com.xzcmapi.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "xzcm_article")
public class Article extends BaseEntity{
    @Column(name = "title")
    @ApiModelProperty(notes = "标题")
    private String title;

    @ApiModelProperty(notes = "更新人")
    @Column(name = "update_id")
    private Long updateId;

    @ApiModelProperty(notes = "更新时间")
    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "content")
    @ApiModelProperty(notes = "内容")
    private String content;

    @Column(name = "type")
    @ApiModelProperty(notes = "类型")
    private Integer type;

    @Column(name = "views")
    @ApiModelProperty(notes = "浏览量")
    private Integer views;

    @Column(name = "forward_amount")
    @ApiModelProperty(notes = "转发量")
    private Integer forwardAmount;

    @Column(name = "status")
    @ApiModelProperty(notes = "状态")
    private Integer status;

    @Column(name = "address")
    @ApiModelProperty(notes = "发布地址")
    private String address;

    @Column(name = "share_title")
    @ApiModelProperty(notes = "分享标题")
    private String shareTitle;

    @Column(name = "share_desc")
    @ApiModelProperty(notes = "分享描述")
    private String shareDesc;

}
