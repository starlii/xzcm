package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "基础信息设置")
@Data
public class BasicSettingModel {

    @ApiModelProperty(notes = "平台名称")
    private String platformName;

    @ApiModelProperty(notes = "平台域名")
    private String platformDomain;

//    private String platformLogo;

//    private String homeImage;

    @ApiModelProperty(notes = "新用户套餐")
    private String newUserPackage;

//    private String newUserLifecycle;
}
