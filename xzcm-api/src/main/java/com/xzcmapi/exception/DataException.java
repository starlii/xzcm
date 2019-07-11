package com.xzcmapi.exception;

import com.xzcmapi.common.CodeMessage;

public class DataException extends XzcmBaseRuntimeException {

    public DataException() {
        super(CodeMessage.DATAERROR);
    }

    public DataException(String message) {
        super(message);
        this.codeMessage = CodeMessage.DATAERROR;
    }
}
