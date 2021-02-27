package com.cloud.console.Controller;

import com.cloud.console.Constants;
import com.cloud.console.encrypt.RSAEncrypt;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/encrypt")
@AllArgsConstructor
@Slf4j
public class EncryptController {
    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/getPublicKey/{userName}")
    public String loadPublicKey(@PathVariable String userName) throws Exception {
        String publicKey = redisTemplate.opsForValue().get(userName + "_" + Constants.PUBLIC_KEY).toString();
        String privateKey = redisTemplate.opsForValue().get(userName + "_" + Constants.PRIVATE_KEY).toString();
        if (StringUtils.isBlank(publicKey) || StringUtils.isBlank(privateKey)) {
            Map<String, String> keyPair = RSAEncrypt.genKeyPair(userName);
            redisTemplate.opsForValue().set(userName + "_" + Constants.PUBLIC_KEY, keyPair.get(Constants.PUBLIC_KEY));
            redisTemplate.opsForValue().set(userName + "_" + Constants.PRIVATE_KEY, keyPair.get(Constants.PRIVATE_KEY));
        }
        return publicKey;
    }
}
