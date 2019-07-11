package com.xzcmapi.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 解压文件
 */
@Data
public class UnZipCaseFileRequest {
    @ApiModelProperty("批次号")
    private String batchNum;
    @ApiModelProperty("文件ID")
    private String uploadFile;
    @ApiModelProperty("公司Code")
    private String companyCode;
}
