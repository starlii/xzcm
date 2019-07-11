package com.xzcmapi.entity;


import com.xzcmapi.annotation.ExcelAnno;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;
import java.io.Serializable;

@Data
@Document
@ApiModel(description = "数据行错误信息")
public class RowError implements Serializable {
    @ApiModelProperty("唯一标识（主键）")
    @Id
    private String id;
    @ApiModelProperty("sheet名称")
    private String sheetName;
    @ApiModelProperty("行数")
    @ExcelAnno(cellName = "Excel行号")
    private Integer rowIndex;
    @ApiModelProperty("错误信息")
    @ExcelAnno(cellName = "错误内容")
    private String errorMsg;
}
