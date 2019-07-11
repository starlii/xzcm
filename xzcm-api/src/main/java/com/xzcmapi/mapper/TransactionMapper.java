package com.xzcmapi.mapper;

import com.xzcmapi.entity.Transaction;
import com.xzcmapi.model.TodayDealsModel;
import com.xzcmapi.util.MyMapper;

public interface TransactionMapper extends MyMapper<Transaction> {

    TodayDealsModel getTodayDeals();
}
