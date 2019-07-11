package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "文章model")
public class ArticleModel {

    private Long id;

    @ApiModelProperty(notes = "标题")
    private String title;

    @ApiModelProperty(value = "分享标题")
    private String shareTitle;

    @ApiModelProperty(value = "分享描述")
    private String shareDesc;

    @ApiModelProperty(notes = "内容")
    private String content;

    @ApiModelProperty(notes = "类型")
    private Integer type;

    @ApiModelProperty(notes = "浏览量")
    private Integer views;

    @ApiModelProperty(notes = "转发量")
    private Integer forwardAmount;

    @ApiModelProperty(notes = "状态")
    private int status;

    @ApiModelProperty(notes = "发布地址")
    private String address;

    @ApiModelProperty(notes = "图片地址")
    private List<String> urls;

}
