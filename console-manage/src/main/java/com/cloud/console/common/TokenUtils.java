package com.cloud.console.common;

import com.alibaba.fastjson.JSON;
import com.nimbusds.jose.JWSObject;

import java.text.ParseException;

public class TokenUtils {

    public static String getUserName(String token) throws ParseException {
        JWSObject tokenObject = JWSObject.parse(token);
        String userInfo = tokenObject.getPayload().toString();
        return JSON.parseObject(userInfo).getString("user_name");

    }

    public static String getUserId(String token) throws ParseException {
        JWSObject tokenObject = JWSObject.parse(token);
        String userInfo = tokenObject.getPayload().toString();
        return JSON.parseObject(userInfo).getString("user_name");
    }
}
