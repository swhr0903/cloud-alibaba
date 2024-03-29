package com.cloud.console.service;

import com.cloud.console.po.User;
import com.cloud.console.vo.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Frank on 2017/8/7. 自定义UserDetailsService
 */
@Service
public class UserDetailsService
        implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    SecurityService securityService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.cloud.console.vo.User params = new com.cloud.console.vo.User();
        params.setUsername(username);
        params.setStatus(1);
        User user = securityService.getUser(params);
        if (user == null) {
            throw new UsernameNotFoundException("帐号不存在");
        } else {
            redisTemplate.opsForValue().set(username, user);
        }
        List<UserRole> roles = securityService.getUserRoles(username);
        return new UserDetails(user, roles);
    }
}
