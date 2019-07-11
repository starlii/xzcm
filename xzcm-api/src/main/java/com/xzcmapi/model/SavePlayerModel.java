package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(description = "保存选手对象")
public class SavePlayerModel{

    @ApiModelProperty(notes = "活动id -- 必传",required = true)
    private Long activityId;

    @ApiModelProperty(notes = "选手姓名 -- 必填")
    private String name;

    @ApiModelProperty(notes = "选手电话 -- 必填")
    private String phone;

    @ApiModelProperty(notes = "选手比赛宣言 -- 必填")
    private String declaration;

    @ApiModelProperty(notes = "票数")
    private Integer votes;

    @ApiModelProperty(notes = "礼物")
    private BigDecimal gift;

    @ApiModelProperty(notes = "描述")
    private String playerDesc;

    @ApiModelProperty(notes = "图片地址 -- 不传")
    private String image;

    @ApiModelProperty(notes = "选手图片集合")
    private List<String> images;


    @ApiModelProperty(notes = "审核状态")
    private Integer approvalStatus;


    @ApiModelProperty(notes = "平台类型 1--admin  2--h5")
    private Integer deviceType;

    @ApiModelProperty(notes = "openId")
    private String openId;

}
