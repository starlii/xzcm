package com.xzcmapi.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "xzcm_activity_transaction")
public class Transaction extends BaseEntity{

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "transaction_amount")
    private BigDecimal transactionAmount;

    @Column(name = "transaction_time")
    private Date transactionTime;

    @Column(name = "player_id")
    private Long playerId;

    @Column(name = "activity_id")
    private Long activityId;
}
