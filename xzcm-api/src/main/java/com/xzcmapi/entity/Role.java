package com.xzcmapi.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Table(name = "xzcm_system_role")
public class Role extends BaseEntity {

    public static final Integer BASE_VALUE_ID = 16;
    public static final Integer BASH_VALUE_SORT = 10000;
    public static final Integer BASE_VALUE_PID = 0;

    //超级管理员标识
    @ApiModelProperty(notes = "超级管理员标识")
    public static final String ROLE_TYPE = "ROEL_ADMIN";

    /**
     * 角色名
     */
    @NotNull
    @ApiModelProperty(value = "角色名", notes = "角色名")
    @Column(name = "role_name")
    private String name;

    /**
     * 角色状态
     */
    @ApiModelProperty(value = "角色状态", notes = "角色状态0启用1禁用")
    @Column(name = "role_status")
    private Integer status;


    @ApiModelProperty(value = "是否是管理员角色",notes = "1--管理员 2--职员")
    @Column(name = "is_manager")
    private Integer isManager;

    /**
     * 备注
     */
    @Size(max = 800, message = "备注不能超过800字符")
    @ApiModelProperty(value = "备注", notes = "备注")
    @Column(name = "role_remark")
    private String remark;

    @Transient
    @ApiModelProperty(value = "角色的权限信息", notes = "角色的权限信息")
    private List<Permission> roleResource;

    public enum Manager{
        MANAGER(1,"manager"),STAFF(2,"staff");
        private Integer value;
        private String remark;

        Manager(Integer value, String remark) {
            this.value = value;
            this.remark = remark;
        }

        public Integer getValue() {
            return value;
        }

        public String getRemark() {
            return remark;
        }
    }
}
