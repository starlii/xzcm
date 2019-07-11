package com.xzcmapi.util.auth;

import com.xzcmapi.common.Constant;
import com.xzcmapi.util.Base64Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Component("tokenManager")
public class RedisTokenManager implements TokenManager {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setRedis(StringRedisTemplate redis) {
        this.stringRedisTemplate = redis;
        //泛型设置成Long后必须更改对应的序列化方案
        redis.setKeySerializer(new JdkSerializationRedisSerializer());
    }

//    public TokenModel createToken(long userId) {
//        //使用uuid作为源token
//        String token = UUID.randomUUID().toString().replace("-", "");
//        TokenModel model = new TokenModel(userId, token);
//        //存储到redis并设置过期时间
//        stringRedisTemplate.boundValueOps(String.valueOf(userId)).set(token, Constant.TOKEN_EXPIRES_MINUTES, TimeUnit.HOURS);
//        return model;
//    }

    public String createToken(long userId) {
        //使用uuid作为源token
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String token = UUID.randomUUID().toString().replace("-", "");
        //使用userId和登录的时间戳作为key
        String key = String.format("%s_%s",userId,timeStamp);
        String tokenKey = Base64Util.encoder(key);

        //使用key和token组成最终的token
        String valiToken = String.format("%s_%s",tokenKey,token);
        //存储到redis并设置过期时间
        stringRedisTemplate.boundValueOps(String.valueOf(tokenKey)).set(token, Constant.TOKEN_EXPIRES_MINUTES, TimeUnit.MINUTES);
        return valiToken;
    }

    public TokenModel getToken(String authentication) {
        if(StringUtils.isEmpty(authentication)){
            return null;
        }
        String[] param = authentication.split("_");
        if (param.length != 2) {
            return null;
        }
        //使用userId和源token简单拼接成的token，可以增加加密措施
        String tokenKey = Base64Util.decoder(param[0]);
        String[] keys = tokenKey.split("_");
        if(keys.length != 2){
            return null;
        }
        long userId = Long.parseLong(keys[0]);
        String token = param[1];
        return new TokenModel(userId, token,param[0]);
    }

    public boolean checkToken(TokenModel model) {
        if (model == null) {
            return false;
        }
        String token = stringRedisTemplate.boundValueOps(String.valueOf(model.getKey())).get();
        //if (token == null || !token.equals(model.getToken())) {
        if (token == null) {
                return false;
        }
        //如果验证成功，说明此用户进行了一次有效操作，延长token的过期时间
            stringRedisTemplate.boundValueOps(String.valueOf(model.getKey())).expire(Constant.TOKEN_EXPIRES_MINUTES, TimeUnit.MINUTES);
        return true;
    }

    public void deleteToken(long userId) {
        stringRedisTemplate.delete(String.valueOf(userId));
    }
}
