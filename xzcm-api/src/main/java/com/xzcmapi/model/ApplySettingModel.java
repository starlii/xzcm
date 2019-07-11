package com.xzcmapi.model;

import com.xzcmapi.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@ApiModel(description = "活动报名设置参数")
public class ApplySettingModel{

    @ApiModelProperty(notes = "最少上传图片数量")
    private Integer miniImage;

    @ApiModelProperty(notes = "最多上传图片")
    private Integer maxImage;

    @ApiModelProperty(notes = "报名字段")
    private String applyField;

    @ApiModelProperty(notes = "其它报名字段")
    private String otherField;

    @ApiModelProperty(notes = "报名页面提示文字")
    private String applyTips;

}
