package com.xzcmapi.exception;


import com.xzcmapi.common.CodeMessage;

public class XzcmBaseRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 5270174268213622465L;

	protected CodeMessage codeMessage;

	public XzcmBaseRuntimeException(String message) {
		super(message);
	}

	public XzcmBaseRuntimeException(CodeMessage codeMessage) {
		super(codeMessage.getMsg());
		this.codeMessage = codeMessage;
	}
	public XzcmBaseRuntimeException(CodeMessage codeMessage, String message) {
		this.codeMessage = codeMessage;
		this.codeMessage.setMsg(message);
	}
	public CodeMessage getCodeMessage() {
		return codeMessage;
	}

	public void setCodeMessage(CodeMessage codeMessage) {
		this.codeMessage = codeMessage;
	}

}
