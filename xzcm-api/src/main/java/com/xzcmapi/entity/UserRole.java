package com.xzcmapi.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Table(name = "xzcm_system_roluser")
public class UserRole extends BaseEntity{

    @ApiModelProperty(value = "用户ID",notes = "用户ID")
    @Column(name = "user_id")
    private Long userId;

    @ApiModelProperty(value = "角色ID",notes = "角色ID")
    @Column(name = "role_id")
    private Long roleId;
}
