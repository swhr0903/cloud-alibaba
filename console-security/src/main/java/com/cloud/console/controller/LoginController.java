package com.cloud.console.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.cloud.console.Constants;
import com.cloud.console.common.Result;
import com.cloud.console.encrypt.RSAEncrypt;
import com.cloud.console.service.UserDetails;
import com.cloud.console.service.UserDetailsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class LoginController {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserDetailsService userDetailsService;

    @PostMapping("/password")
    public Result auth(String userName, String password) throws Exception {
        Result result = new Result();
        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            String privateKey = (String) redisTemplate.opsForValue().get(userName + "_" + Constants.PRIVATE_KEY);
            Base64 base64 = new Base64();
            String decoded = new String(RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(privateKey),
                    base64.decode(password)));
            try {
                UserDetails user = userDetailsService.loadUserByUsername(userName);
                if (!BCrypt.checkpw(decoded, user.getPassword().replace(Constants.BCRYPT, Strings.EMPTY))) {
                    throw new BadCredentialsException("密码错误");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.setCode("1");
            result.setMsg("登陆验证通过");
        } else {
            result.setCode("0");
            result.setMsg("请输入用户名及密码");
        }
        return result;
    }
}
