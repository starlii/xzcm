package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(description = "礼物model")
public class GiftModel {
    @ApiModelProperty(notes = "礼物名称")
    private String giftName;

    @ApiModelProperty(notes = "礼物价格")
    private BigDecimal giftPrice;

    @ApiModelProperty(notes = "礼物票数")
    private Integer giftVotes;

    @ApiModelProperty(notes = "礼物图片")
    private String giftImage;
}
