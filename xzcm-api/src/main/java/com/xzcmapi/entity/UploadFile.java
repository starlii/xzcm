package com.xzcmapi.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;


@ApiModel(value = "UploadFile",
        description = "上传文件信息")
@Data
@Document
public class UploadFile implements Serializable{
    @Id
    private String id;
    @ApiModelProperty(notes = "文件名称")
    private String realName;
    @ApiModelProperty(notes = "文件类型")
    private String type;
    @ApiModelProperty(notes = "存放路径")
    private String url;
    @ApiModelProperty(notes = "内网路径")
    private String localUrl;
    @ApiModelProperty(notes = "文件别名")
    private String name;
    @ApiModelProperty(notes = "文件大小")
    private Long size;
    @ApiModelProperty(notes = "创建时间")
    private Date createTime;
    @ApiModelProperty(notes = "创建者")
    private String creator;
    @ApiModelProperty(notes = "图片文件批次号")
    private String batchNum;
}
