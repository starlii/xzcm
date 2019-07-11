package com.xzcmapi.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "xzcm_user_package")
public class UserPackage extends BaseEntity{

    @Column(name = "name")
    @ApiModelProperty(notes = "套餐名称")
    private String name;

    @Column(name = "description")
    @ApiModelProperty(notes = "描述")
    private String description;

    @Column(name = "status")
    @ApiModelProperty(notes = "状态")
    private Integer status;

    @Column(name = "agent_value")
    @ApiModelProperty(notes = "代理系数")
    private Double agentValue;
}
