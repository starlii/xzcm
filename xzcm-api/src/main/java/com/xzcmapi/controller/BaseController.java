package com.xzcmapi.controller;

import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import com.xzcmapi.common.StringEditor;
import io.swagger.models.auth.In;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BaseController {

	protected final transient Log log = LogFactory.get(this.getClass());

	protected static final String FAILURE = "failure";
	protected static final String SUCCESS = "success";

	protected static final Integer SUCCESS_CODE = 200;
	protected static final Integer DATA_ERROR = 502;
	protected static final Integer BAD_REQUEST = 400;
	protected static final Integer NOT_FOUND = 404;
	protected static final Integer FORBBIDEN = 403;
	protected static final Integer INTERNAL_ERROR = 500;
	protected static final Integer PERMISSION_ERROR = 503;


	/**
	 * 默认页为1
     */
	protected static final Integer PAGENUM = 1;
	/**
	 * 页码大小10
     */
	protected static final Integer PAGESIZE = 10;

	/**
	 * ajax
	 * 提示常量
	 */
	protected static final String SUCCESS_LOAD_MESSAGE = "加载成功!";
	protected static final String FAILURE_LOAD_MESSAGE = "加载失败!";
	/**
	 * 保存
	 * 提示常量
	 */
	protected static final String SUCCESS_SAVE_MESSAGE = "保存成功!";
	protected static final String FAILURE_SAVE_MESSAGE = "保存失败!";
	/**
	 * 更新
	 * 提示常量
	 */
	protected static final String SUCCESS_UPDATE_MESSAGE = "更新成功!";
	protected static final String FAILURE_UPDATE_MESSAGE = "更新失败!";
	/**
	 * 充值
	 * 提示常量
	 */
	protected static final String SUCCESS_CREDIT_MESSAGE = "充值成功!";
	protected static final String FAILURE_CREDIT_MESSAGE = "充值失败!";
	/**
	 * 删除
	 * 提示常量
	 */
	protected static final String SUCCESS_DELETE_MESSAGE = "删除成功!";
	protected static final String FAILURE_DELETE_MESSAGE = "删除失败!";
	protected static final String WARNING_DELETE_MESSAGE = "已经删除!";
	/**
	 * 禁用启用
	 */
	protected static final String SUCCESS_ENABLE_TRUE = "启用成功!";
	protected static final String SUCCESS_ENABLE_FALSE = "禁用成功!";
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
		binder.registerCustomEditor(String.class, "password", new StringEditor(true));
	}

	public static String getSession(String key) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		Object obj = request.getSession().getAttribute(key);
		if (obj == null) {
			return null;
		}
		return obj.toString();
	}

	public static void setSession(String key, Object value) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		request.getSession().setAttribute(key, value);
	}

	public static void removeSession(String key) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		request.getSession().removeAttribute(key);
	}
}
