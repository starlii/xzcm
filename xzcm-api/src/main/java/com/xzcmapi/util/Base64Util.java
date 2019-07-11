package com.xzcmapi.util;

import java.nio.charset.Charset;
import java.util.Base64;

public class Base64Util {

    private static final Base64.Encoder encoder = Base64.getEncoder();

    private static final Base64.Decoder decoder = Base64.getDecoder();

    public static String encoder(String origin){
        return encoder.encodeToString(origin.getBytes());
    }

    public static String decoder(String encoderString){
        return new String(decoder.decode(encoderString.getBytes()),Charset.forName("utf-8"));
    }

    public static void main(String[] args) {
        String test = "123456";
        System.out.println(encoder(test));
        System.out.println(decoder(encoder(test)));
    }
}
