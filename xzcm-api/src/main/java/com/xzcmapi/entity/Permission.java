package com.xzcmapi.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Table(name = "xzcm_system_resource")
public class Permission extends BaseEntity{
    /**
     * 系统名称
     */
    @NotNull
    @ApiModelProperty(value = "系统名称",notes = "系统名称")
    @Column(name="reso_appname")
    private String name;
    /**
     * 类型：0,1,2(本系统,内部系统,外部系统)
     */
    @ApiModelProperty(value = "类型：0,1,2(本系统,内部系统,外部系统)",notes = "类型：0,1,2(本系统,内部系统,外部系统)")
    @Column(name="reso_type")
    private Integer type;
    /**
     * 访问url地址
     */
    @ApiModelProperty(value = "访问url地址",notes = "访问url地址")
    @Column(name="reso_path")
    private String url;
    /**
     * 权限代码,多个用逗号分隔
     * menu例子：role:*，button例子：role:create,role:update,role:delete,role:view
     */
    @ApiModelProperty(value = "权限代码,多个用逗号分隔",notes = "权限代码,多个用逗号分隔")
    @Column(name="reso_code")
    private String code;
    /**
     * 父节点id,一级节点为0
     */
    @ApiModelProperty(value = "父节点id,一级节点为0",notes = "父节点id,一级节点为0")
    @Column(name="reso_parentid")
    private Long parentId;

    /**
     * 排序号
     */
    @ApiModelProperty(value = "排序号",notes = "排序号")
    @Column(name="reso_order")
    private Long sort;
    /**
     * 是否禁用  true禁用  false 启用
     */
    @ApiModelProperty(value = "是否禁用  true禁用  false 启用",notes = "是否禁用  true禁用  false 启用")
    @Column(name="reso_status")
    private Integer isLock;

    /**
     * 菜单图标
     */
    @ApiModelProperty(value = "菜单图标",notes = "菜单图标")
    @Column(name="reso_icon")
    private String icon;

    /**
     * 菜单路由
     */
    @ApiModelProperty(value = "菜单路由",notes = "菜单路由")
    @Column(name="reso_remark")
    private String path;

    /**
     * 资源类型  0目录 1菜单 2按钮
     * */
    @ApiModelProperty(value = "资源类型")
    @Column(name = "reso_filetype")
    private Integer resoFileType;

    @Column(name = "reso_name")
    @ApiModelProperty(value = "资源名称")
    private String resoName;

    @Column(name = "reso_level")
    @ApiModelProperty(value = "资源级别")
    private Integer resoLevel;

    @ApiModelProperty(value = "权限值")
    @Column(name = "reso_value")
    private String value;
}
