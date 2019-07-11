package com.xzcmapi.config;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/druid/*",
        initParams={
                //@WebInitParam(name="allow",value="10.238.100.189,127.0.0.1"),// IP白名单
//                @WebInitParam(name="allow",value="127.0.0.1,39.108.248.112,192.168.199.116,10.3.1.222"),// IP白名单
//                @WebInitParam(name="deny",value="192.168.16.111"),// IP黑名单 (存在共同时，deny优先于allow)
                @WebInitParam(name="loginUsername",value="admin"),// 用户名
                @WebInitParam(name="loginPassword",value="xzcm!jhg"),// 密码
                @WebInitParam(name="resetEnable",value="false")// 禁用HTML页面上的“Reset All”功能
        })
public class DruidStatViewServlet extends StatViewServlet {

}