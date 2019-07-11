package com.xzcmapi.annotation;

import java.lang.annotation.*;

/**
 * @Description: Excel导入注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelAnno {
    /**
     * 在excel文件中某列数据的名称
     *
     * @return 名称
     */
    String cellName() default "";

    /**
     * 实体中的字段类型校验
     * @return
     */

    FieldType fieldType() default FieldType.STRING;
    /**
     * 在excel中列的顺序，从小到大排
     *
     * @return 顺序
     */
    int order() default 0;

    /**
     * 特殊字段校验
     * @return
     */
    FieldCheck fieldCheck() default FieldCheck.NONE;

    /**
     * 数据类型校验枚举
     */
    enum FieldType{
        STRING,
        INTEGER,
        DOUBLE,
        DATE
    }

    /**
     * 特殊字段校验枚举
     */
    enum FieldCheck {
        NONE,
        //选手姓名
        PLAYER_NAME,
        //手机号
        PLAYER_PHONE,
        //选手介绍
        PLAYER_DESC,
        PLAYER_ANO

    }
}
