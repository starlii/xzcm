package com.xzcmapi.model;

import javax.validation.constraints.Size;

public class ModifyPwdParam {


    private String oldPwd;

    private String username;

    @Size(min = 6,max = 64,message = "密码长度必须大于6位小于64位")
    private String newPwd;


    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
