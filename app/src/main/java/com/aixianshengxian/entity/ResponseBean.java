package com.aixianshengxian.entity;

/**
 * Created by cwj on 2017/6/5.
 */

public class ResponseBean {
 private int errorCode ;
    private  String  message ;
    private String data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
