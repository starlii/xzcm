package com.xzcmapi.util.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenModel implements java.io.Serializable {

    //用户id
    private long userId;

    //随机生成的uuid
    private String token;

    private String key;

}
