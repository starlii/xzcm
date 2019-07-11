package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@ApiModel(value = "用户model")
public class UserModel {

    private Long id;

    @NotNull
    @Size(min = 2, max = 50, message = "用户名长度不能小于2位大于50位")
    private String username;

    @Size(min = 6, max = 64, message = "密码长度不能小于6位大于64位")
    private String password;

    @ApiModelProperty(value = "确认密码",notes = "确认密码")
    private String repassword;


    private Integer userPackage;

    private Date userUsageStart;
    private Date userUsageEnd;

    @Max(value = 1L, message = "状态不能为空")
    @Min(value = 0L, message = "状态不能为空")
    private Integer status;

    private Date updateTime;

    private String remark;
    private Date userCreateTime;

    @ApiModelProperty(value = "管理员权限标识 0--system 1--manage 2--admin",notes = "管理员权限标识 0--system超级管理员 1--manage管理员 2--admin普通用户")
    private Integer role;
}
