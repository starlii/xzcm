package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "访问连接model")
public class QrCodeModel {

    @ApiModelProperty(notes = "访问连接")
    private String accessUrl;

    @ApiModelProperty(notes = "二维码地址")
    private String qrCodeUrl;
}
