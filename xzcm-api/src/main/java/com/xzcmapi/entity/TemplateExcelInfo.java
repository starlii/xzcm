package com.xzcmapi.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class TemplateExcelInfo {
    @ApiModelProperty("Excel列名称")
    public String cellName;
    @ApiModelProperty("Excel列类型")
    public String cellType;
    @ApiModelProperty("Excel列编号")//数字
    public Integer cellNum;
    @ApiModelProperty("Excel列类型名称")
    public String cellTypeName;
    @ApiModelProperty("Excel列关联字段")
    public String relateName;
    @ApiModelProperty("Excel列编号")
    public String cellCode;
    @ApiModelProperty("Excel列编号")//字母
    public String colNum;
    @ApiModelProperty("是否必输")//0:是  1：否
    public Integer flag;

}
