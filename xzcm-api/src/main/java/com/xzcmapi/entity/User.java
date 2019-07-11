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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@EqualsAndHashCode(callSuper = false)
@Table(name = "xzcm_system_user")
public class User extends BaseEntity {
    /**
     * 用户名
     */
    @NotNull
    @Size(min = 6, max = 50, message = "用户名长度不能小于6位大于50位")
    @ApiModelProperty(value = "用户名", notes = "用户名")
    @Column(name = "user_username")
    private String username;
    /**
     * 密码
     */
    @Size(min = 6, max = 64, message = "密码长度不能小于6位大于64位")
    @ApiModelProperty(value = "密码", notes = "密码")
    @Column(name = "user_password")
    private String password;

    /**
     * 用户套餐
     */
    @ApiModelProperty(value = "用户套餐", notes = "用户套餐")
    @Column(name = "user_package")
    private Integer userPackage;


    /**
     */
    @ApiModelProperty(value = "0-启用  1-未启用", notes = "0-启用  1-未启用")
    @Column(name = "user_status")
    private Integer status;

    /**
     *
     */
    @ApiModelProperty(value = "更新时间")
    @Column(name = "user_updatetime")
    private Date updateTime;

    @ApiModelProperty(value = "使用开始时间")
    @Column(name = "user_usage_start")
    private Date userUsageStart;

    @ApiModelProperty(value = "使用结束更新时间")
    @Column(name = "user_usage_end")
    private Date userUsageEnd;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", notes = "备注")
    @Size(max = 800, message = "备注不能超过800字符")
    @Column(name = "user_remark")
    private String remark;

    @ApiModelProperty(notes = "角色")
    @Column(name = "role")
    private String role;

    @Transient
    private String roleName;

    @Column(name = "role_value")
    private String roleValue;

    @Column(name = "ip")
    @ApiModelProperty(notes = "最后登录ip")
    private String ip;

    @Column(name = "login_time")
    @ApiModelProperty(notes = "最后登录时间")
    private Date loginTime;

    @Transient
    @ApiModelProperty(notes = "套餐名称")
    private String packageName;

    public enum Role{
        SYSTEM(0,"system"),ADMIN(1,"admin"),MANAGE(2,"manager");
        private Integer value;
        private String remark;

        Role(Integer value, String remark) {
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

    public static String getRoleRemark(Integer role){
        Map<Integer,String> map = new HashMap<>();
        map.put(0,"system");
        map.put(2,"manager");
        map.put(1,"admin");
        return map.get(role);
    }

    public static Integer getRoleValue(String role){
        Map<String,Integer> map = new HashMap<>();
        map.put("system",0);
        map.put("manager",2);
        map.put("admin",1);
        return map.get(role);
    }


}
