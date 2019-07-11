package com.xzcmapi.util;

import com.xiaoleilu.hutool.crypto.SecureUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    //盐
    public final static String SALT = "XZCM415";

    /**
     * 密码加密
     * 算法 -> md5(password + salt)
     *
     * @param password 密码
     * @return 加密后的密码
     */
    public static String encrypt(String password) {
        return SecureUtil.md5(password + SALT);
    }

    public static void main(String[] args) {
        String str = "ok123";
        System.out.println(encrypt(str));
        System.out.println(SecureUtil.md5().digestHex(str));
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        passwordEncoder.matches(MD5.MD5Encode("ok123123"), "1f8e43fd935911e09998948f2217fc48");
        System.out.println();
    }

}
