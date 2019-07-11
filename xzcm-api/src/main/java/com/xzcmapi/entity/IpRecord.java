package com.xzcmapi.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "xzcm_system_log")
public class IpRecord extends BaseEntity{
    @Column(name = "ip")
    private String ip;
}
