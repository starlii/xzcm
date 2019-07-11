package com.xzcmapi.common;

import java.io.Serializable;

public class ResponseResult<T> implements Serializable {

    private Boolean error;
    private String msg;
    private Integer code;
    private T data;

    public ResponseResult() {
    }

    public ResponseResult(Boolean error, String msg, Integer code, T data) {
        this.error = error;
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Boolean error, Integer code, String msg) {
        this.error = error;
        this.msg = msg;
        this.code = code;
    }


    public static ResponseResult toResponseResult(Object data) {
        return new ResponseResult<>(false,CodeMessage.SUCCESS.getMsg(),CodeMessage.SUCCESS.getCode(),data);
    }

    public ResponseResult(Boolean error, String msg, T data) {
        this.error = error;
        this.msg = msg;
        this.data = data;
    }

    public ResponseResult(Boolean error, Integer code, T data) {
        this.error = error;
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Boolean error, T data) {
        this.error = error;
        this.data = data;
    }


    public Boolean isError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResponseResult<?> that = (ResponseResult<?>) o;

        if (error != null ? !error.equals(that.error) : that.error != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (msg != null ? !msg.equals(that.msg) : that.msg != null) return false;
        return data != null ? data.equals(that.data) : that.data == null;
    }

    @Override
    public int hashCode() {
        int result = error != null ? error.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (msg != null ? msg.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "error='" + error + '\'' +
                ", msg='" + msg + '\'' +
                ", code=" + code +
                ", data=" + data +
                '}';
    }
}
