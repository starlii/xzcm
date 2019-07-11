package com.xzcmapi.common;

public enum Status {
    // 0-启用 1-禁用
    ENABLE(0),DISABLE(1);
    private Integer status;
    Status(Integer status){
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}

