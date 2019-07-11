package com.xzcmapi.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "xzcm_system_param")
public class SystemParam extends BaseEntity{

    @Column(name = "param_key")
    private String key;

    @Column(name = "param_value")
    private String value;

    @Column(name = "type")
    private Integer type;

    @Column(name = "status")
    private Integer status;
}
