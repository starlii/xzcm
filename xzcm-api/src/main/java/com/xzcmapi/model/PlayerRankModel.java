package com.xzcmapi.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "选手排名model")
@Data
public class PlayerRankModel {

    @ApiModelProperty(notes = "选手id")
    private Long playerId;

    @ApiModelProperty(notes = "姓名")
    private String name;

    @ApiModelProperty(notes = "排名")
    private Integer rank;

    @ApiModelProperty(notes = "票数")
    private Integer votes;

    @ApiModelProperty(notes = "图片")
    private String image;
}
