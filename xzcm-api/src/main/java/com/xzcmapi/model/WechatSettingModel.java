package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "微信设置")
public class WechatSettingModel {

    @ApiModelProperty("公众号名称")
    private String officialAccount;

    @ApiModelProperty("公众号二维码")
    private String ccountImage;

    @ApiModelProperty("AppID")
    private String appId;

    @ApiModelProperty("AppSecret")
    private String appSecret;

    @ApiModelProperty("微支付商户号")
    private String payAccount;

    @ApiModelProperty("微支付API密钥")
    private String payRsa;

    @ApiModelProperty("证书文件apiclient_cert")
    private String apiclientCert;

    @ApiModelProperty("证书文件apiclient_key")
    private String apiclientKey;

    @ApiModelProperty("证书文件rootca")
    private String rootca;

    @ApiModelProperty("关注链接")
    private String follow;

    @ApiModelProperty("订单实时信息")
    private String orderMessage;
}
