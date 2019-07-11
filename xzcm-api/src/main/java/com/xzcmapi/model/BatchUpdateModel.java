package com.xzcmapi.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;
import java.util.List;

@Data
@ApiModel(description = "批量更新备注")
public class BatchUpdateModel{

    @ApiModelProperty(notes = "ids")
    private List<Long> ids;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "备注2")
    private String remarkSec;
}
