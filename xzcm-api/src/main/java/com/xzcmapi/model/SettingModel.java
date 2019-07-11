package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "平台设置")
public class SettingModel {

    @ApiModelProperty(notes = "基础信息设置")
    private BasicSettingModel basicSetting;

    @ApiModelProperty(notes = "微信设置")
    private WechatSettingModel wechatSetting;

//    @ApiModelProperty(notes = "七牛设置")
//    private QiNiuSettingModel qiNiuSetting;
}
