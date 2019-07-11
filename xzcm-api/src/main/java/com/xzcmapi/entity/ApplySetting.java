package com.xzcmapi.entity;

import com.sun.xml.internal.rngom.parse.host.Base;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "xzcm_apply_setting")
public class ApplySetting extends BaseEntity {
    @ApiModelProperty(notes = "最少上传图片数量")
    @Column(name = "mini_image")
    private Integer miniImage;
    @Column(name = "max_image")
    @ApiModelProperty(notes = "最多上传图片")
    private Integer maxImage;
    @Column(name = "apply_field")
    @ApiModelProperty(notes = "报名字段")
    private String applyField;
    @Column(name = "other_field")
    @ApiModelProperty(notes = "其它报名字段")
    private String otherField;
    @Column(name = "apply_tips")
    @ApiModelProperty(notes = "报名页面提示文字")
    private String applyTips;

    @Column(name = "activity_id")
    @ApiModelProperty(notes = "外键：保存时不需要传")
    private Long activityId;
}
