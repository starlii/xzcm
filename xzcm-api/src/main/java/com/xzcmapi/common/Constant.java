package com.xzcmapi.common;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constant {

    /**
     * 定时任务状态
     */
    public enum JobStatus {

        /**
         * 暂停
         */
        PAUSE(0),
        /**
         * 正常
         */
        NORMAL(1);


        private int value;

        private JobStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 表单token
     */
    public static final String TOKEN_FORM = "tokenForm";

    /**
     * Ajax操作验证表单重复后的响应头key
     */
    public static final String HEAD_TOKEN_FORM_KEY = "X-Form-Token";

    /**
     * Ajax操作验证表单重复后的响应头value
     */
    public static final String HEAD_TOKEN_FORM_VALUE = "Repeat-Submit";

    /**
     * Ajax操作没有权限的响应头key
     */
    public static final String HEAD_NO_PERMISSION_KEY = "X-No-Permission";

    /**
     * Ajax操作没有权限的响应头value
     */
    public static final String HEAD_NO_PERMISSION_VALUE = "No-Permission";

    /**
     * Ajax操作登陆超时的响应头key
     */
    public static final String HEAD_SESSION_STATUS_KEY = "X-Session-Status";

    /**
     * Ajax操作登陆超时的响应头value
     */
    public static final String HEAD_SESSION_STATUS_VALUE = "Session-Timeout";

    /**
     * 存储当前登录用户id的字段名
     */
    public static final String CURRENT_USER_ID = "CURRENT_USER_ID";

    /**
     * token有效期（分钟）
     */
    public static final int TOKEN_EXPIRES_MINUTES = 120;

    public static final int VER_CODE_TIME = 1;

    /**
     * 存放Authorization的header字段
     */
    public static final String AUTHORIZATION = "authorization";

    public static final String EXCEL_TYPE_XLS = "xls";
    public static final String EXCEL_TYPE_XLSX = "xlsx";
    public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    public static List<String> directoryName(){
        return  Arrays.asList("king","arthas","cenarius","archimonde","llidan","tyrande","stormrage","onyxia","nefarian");
    }

}
