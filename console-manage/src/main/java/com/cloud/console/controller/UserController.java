package com.cloud.console.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.alibaba.fastjson.JSON;
import com.cloud.console.Constants;
import com.cloud.console.Result;
import com.cloud.console.common.Constant;
import com.cloud.console.common.Logger;
import com.cloud.console.common.TokenUtils;
import com.cloud.console.encrypt.RSAEncrypt;
import com.cloud.console.po.User;
import com.cloud.console.service.Paging;
import com.cloud.console.service.UserService;
import com.cloud.console.vo.UserRole;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.security.interfaces.RSAPrivateKey;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Frank on 2017/8/8.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserService userService;
    @Autowired
    Logger logger;

    @GetMapping("/getUsers")
    public Paging getUsers(
            Integer limit,
            Integer offset,
            String sortName,
            String sortOrder,
            String username,
            String startTime,
            String endTime)
            throws Exception {
        return userService.getUsers(limit, offset, sortName, sortOrder, username, startTime, endTime);
    }

    @GetMapping("/getUser")
    public User getUser(com.cloud.console.vo.User params) {
        User user = userService.getUser(params);
        user.setPassword(null);
        return user;
    }

    @GetMapping("getUserRoles")
    public List<UserRole> getUserRoles(String param) {
        return userService.getUserRoles(param);
    }

    @GetMapping(value = "/isExist/{userName}")
    public Result isExist(@PathVariable String userName) {
        com.cloud.console.vo.User params = new com.cloud.console.vo.User();
        params.setUsername(userName);
        User user = userService.getUser(params);
        Result result = new Result();
        if (user != null) {
            result.setCode("0");
            result.setMsg("帐号已存在");
        } else {
            result.setCode("1");
            result.setMsg("notExist");
        }
        return result;
    }

    @PostMapping(value = "/register")
    public Result register(User user) throws Exception {
        Result result = new Result();
        if (user != null && StringUtils.isNotBlank(user.getUsername()) && StringUtils.isNotBlank(user.getPassword())) {
            Base64 base64 = new Base64();
            RSAPrivateKey privateKey = RSAEncrypt.loadPrivateKeyByStr((String) redisTemplate.opsForValue().get(user.getUsername() +
                    "_" + Constants.PRIVATE_KEY));
            String password = new String(RSAEncrypt.decrypt(privateKey, base64.decode(user.getPassword())));
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            userService.insertUser(user);
            result.setCode("1");
            result.setMsg("注册成功");
        } else {
            result.setCode("1");
            result.setMsg("注册失败，用户资料不全");
        }
        logger.log(user.getUsername() + "注册成功");
        return result;
    }

    @PatchMapping("/update")
    public Result edit(User user) throws Exception {
        Result result = new Result();
        if (user != null && StringUtils.isNotBlank(user.getUsername())) {
            userService.updateUser(user);
        }
        logger.log("编辑用户" + JSON.toJSONString(user));
        result.setCode("1");
        result.setMsg("修改成功");
        return result;
    }

    @DeleteMapping("/del")
    public Result del(@RequestBody List<User> users) throws Exception {
        Result result = new Result();
        userService.delUser(users);
        logger.log("删除用户" + JSON.toJSONString(users));
        result.setCode("1");
        result.setMsg("修改成功");
        return result;
    }

    @PostMapping("/author")
    public Result auth(Long userId, String roles) throws Exception {
        Result result = new Result();
        userService.auth(userId, roles);
        logger.log("授予" + userId + "角色" + roles);
        result.setCode("1");
        result.setMsg("授权成功");
        return result;
    }

    @PostMapping("/uploadImg")
    public Result uploadImg(HttpServletRequest request) throws Exception {
        Result result = new Result();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = null;
        Map map = multipartRequest.getFileMap();
        for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
            Object obj = i.next();
            multipartFile = (MultipartFile) map.get(obj);
        }
        String userName = TokenUtils.getUserName(request.getHeader(Constant.TOKEN_HEADER_STRING));
        String filePath = "avatar/";
        userService.saveImg(filePath, userName, multipartFile);
        result.setCode("1");
        result.setMsg("上传成功");
        return result;
    }
}
