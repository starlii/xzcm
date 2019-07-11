package com.xzcmapi.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@ApiModel(value = "角色权限",description = "rplePermission")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "xzcm_system_rolres")
public class RolePermission extends BaseEntity{
    @ApiModelProperty(value = "角色ID",notes = "角色ID")
    @Column(name="role_id")
    private Long roleId;
    @ApiModelProperty(value = "权限ID",notes = "权限ID")
    @Column(name="reso_id")
    private Long permissionId;
}
