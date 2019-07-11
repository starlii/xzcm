package com.xzcmapi.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "礼物支付model")
public class GiftPayModel {

    @ApiModelProperty(notes = "礼物id,调用送礼物接口返回")
    private Long gitId;
}
