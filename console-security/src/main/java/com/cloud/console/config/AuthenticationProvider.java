package com.cloud.console.config;

import cn.hutool.crypto.digest.BCrypt;
import com.cloud.console.Constants;
import com.cloud.console.encrypt.RSAEncrypt;
import com.cloud.console.service.UserDetails;
import com.cloud.console.service.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Created by Frank on 2017/8/7. Spring Security自定义验证
 */
@Component
@Slf4j(topic = "login")
public class AuthenticationProvider
        implements org.springframework.security.authentication.AuthenticationProvider {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());
        String privateKey = (String) redisTemplate.opsForValue().get(userName + "_" + Constants.PRIVATE_KEY);
        Base64 base64 = new Base64();
        String decodedPwd = null;
        try {
            decodedPwd = new String(RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(privateKey),
                    base64.decode(password)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserDetails user = userDetailsService.loadUserByUsername(userName);
        if (user == null) {
            throw new BadCredentialsException("帐号不存在");
        }
        if (decodedPwd != null) {
            if (!BCrypt.checkpw(decodedPwd, user.getPassword().replace(Constants.BCRYPT, Strings.EMPTY))) {
                throw new BadCredentialsException("密码错误");
            }
            log.info("用户" + user.getUsername() + "登录成功");
        }
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(user, decodedPwd, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
