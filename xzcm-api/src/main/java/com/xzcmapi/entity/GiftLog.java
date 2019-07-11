package com.xzcmapi.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;


@Data
@ApiModel(description = "礼物记录")
@Table(name = "xzcm_gift_log")
public class GiftLog extends BaseEntity{

    @Column(name = "name")
    @ApiModelProperty(notes = "投票人id")
    private String name;

    @Column(name = "open_id")
    @ApiModelProperty(notes = "微信openId")
    private String openId;

    @Column(name = "gift")
    @ApiModelProperty(notes = "礼物")
    private String gift;

    @Column(name = "gift_amount")
    @ApiModelProperty(notes = "礼物金额")
    private BigDecimal giftAmount;

    @Column(name = "order_id")
    @ApiModelProperty(notes = "订单id")
    private String orderId;

    @Column(name = "business_id")
    @ApiModelProperty(notes = "商户号")
    private String businessId;

    @Column(name = "status")
    @ApiModelProperty(notes = "状态")
    private Integer status;

    @Column(name = "ip")
    @ApiModelProperty(notes = "ip地址")
    private String ip;

    @Column(name = "player_id")
    @ApiModelProperty(notes = "关联选手id")
    private Long playerId;

    @Transient
    @ApiModelProperty(notes = "选手姓名")
    private String playerName;

    @Column(name = "activity_id")
    private Long activityId;

    @Transient
    private String activityName;
}
