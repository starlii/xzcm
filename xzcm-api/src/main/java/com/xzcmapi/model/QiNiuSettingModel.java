package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "七牛设置")
public class QiNiuSettingModel {

    @ApiModelProperty(notes = "开启接口")
    private String isOn;

    @ApiModelProperty(notes = "AccessKey")
    private String accessKey;

    @ApiModelProperty(notes = "SecretKey")
    private String secretKey;

    @ApiModelProperty(notes = "空间名称")
    private String spaceName;

    @ApiModelProperty(notes = "七牛测试域名")
    private String testDomain;
}
