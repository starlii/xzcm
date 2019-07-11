package com.xzcmapi.entity;

import com.xzcmapi.annotation.ClassFeature;
import com.xzcmapi.annotation.ExcelAnno;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: Excel数据导入实体接收类
 */
@ApiModel(value = "DataInfoExcelModel",
        description = "Excel数据导入实体类")
@Data
@Document
@ClassFeature(name = "Excel数据导入实体类")
public class DataInfoExcel implements Serializable {
    @Id
    @ApiModelProperty(notes = "数据主键")
    private String id;

    @ApiModelProperty(notes = "选手姓名")
    @ExcelAnno(cellName = "名称",
            fieldCheck = ExcelAnno.FieldCheck.PLAYER_NAME)
    private String name;

    @ApiModelProperty(notes = "手机号码")
    @ExcelAnno(cellName = "手机号码",
            fieldCheck = ExcelAnno.FieldCheck.PLAYER_PHONE)
    private String phone;

    @ApiModelProperty(notes = "选手地址")
    @ExcelAnno(cellName = "地址")
    private String declaration;

    @ApiModelProperty(notes = "选手介绍")
    @ExcelAnno(cellName = "选手介绍")
    private String playerDesc;

    @ApiModelProperty("颜色：0-无色，1-红色，2-黄色")
    private Integer color = new Integer(0);



    /**
     * 导入错误提示颜色
     */
    public enum Color {
        NONE(0,"无色"),
        RED(1,"红色"),
        YELLOW(2,"黄色");

        private Integer value;
        private String remark;

        Color(Integer value, String remark) {
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

}
