package com.xzcmapi.common;


import com.xiaoleilu.hutool.json.JSONObject;

import java.util.Date;

public enum CodeMessage {
	SUCCESS(200, "加载成功"),
	FAILURE(500,"failure"),
	PEMISSIONERROR(501,"权限异常，当前用户无权限！"),
	DATAERROR(502,"数据异常！"),
	PARAMSERROR(504,"参数错误！"),
	REPEATREGISTRATION(505,"重复注册！"),
	FILETYPEERROR(506,"数据文件为非Excel数据"),
	FORCEERROR(507,"Excel文件存在严重错误，请检查模版！"),
	VOTEERROR(508,"投票超过当日限制！"),
	GIFTREEOR(509,"礼物金额超过限制！");

	private Integer code;
	private String msg;
	private CodeMessage(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public JSONObject getOutput(JSONObject output){
		output.put("code", this.code);
		output.put("msg", this.msg);
		output.put("serverTime", new Date().getTime());
		return output;
	}
	public JSONObject getOutput(){
		JSONObject output = new JSONObject();
		output.put("code", this.code);
		output.put("msg", this.msg);
		output.put("serverTime", new Date().getTime()); 
		return output;
	}
	
	public JSONObject getOutput(JSONObject output, String msg){
		output.put("code", this.code);
		output.put("msg", msg);
		output.put("serverTime", new Date().getTime());
		return output;
	}
	public JSONObject getOutput(String msg){
		JSONObject output = new JSONObject();
		output.put("code", this.code);
		output.put("msg", msg);
		output.put("serverTime", new Date().getTime());
		return output;
	}
	
	public static JSONObject getOutput(String code, String msg){
		JSONObject output = new JSONObject();
		output.put("code", code);
		output.put("msg", msg);
		output.put("serverTime", new Date().getTime());
		return output;
	}
}
