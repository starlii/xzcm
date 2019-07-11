package com.xzcmapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document
@ApiModel(value = "DataImportRecord", description = "导入信息")
public class DataImportRecord implements Serializable {

    @ApiModelProperty(notes = "文件ID -- 必传", required = true)
    private String fileId;

    @ApiModelProperty(notes = "活动id -- 必传",required = true)
    private Long activityId;

    @ApiModelProperty(notes = "文件批次号")
    private String batchNum;

}
