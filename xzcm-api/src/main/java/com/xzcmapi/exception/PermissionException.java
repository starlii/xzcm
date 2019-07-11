package com.xzcmapi.exception;

import com.xzcmapi.common.CodeMessage;

public class PermissionException extends XzcmBaseRuntimeException {

    public PermissionException() {
        super(CodeMessage.PEMISSIONERROR);
    }

    public PermissionException(String message) {
        super(message);
        this.codeMessage = CodeMessage.PEMISSIONERROR;
    }
}
