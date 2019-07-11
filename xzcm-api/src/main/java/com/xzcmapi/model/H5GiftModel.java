package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
@ApiModel(description = "h5礼物model")
public class H5GiftModel {
    @ApiModelProperty(notes = "选手id")
    private Long playerId;

//    @ApiModelProperty(notes = "微信名称")
//    private String name;

    @ApiModelProperty(notes = "微信openId")
    private String openId;

    @ApiModelProperty(notes = "礼物id")
    private Long giftId;
//    @ApiModelProperty(notes = "礼物")
//    private String gift;

    @ApiModelProperty(notes = "礼物金额")
    private BigDecimal giftAmount;

//    @ApiModelProperty(notes = "订单id")
//    private String orderId;
//
//    @ApiModelProperty(notes = "商户号")
//    private String businessId;
//
//    @ApiModelProperty(notes = "支付状态 0--未支付 1--已支付")
//    private Integer status;
}
