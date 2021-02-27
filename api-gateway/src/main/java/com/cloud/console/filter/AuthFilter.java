package com.cloud.console.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cloud.console.security.Constants;
import com.cloud.console.security.ResultCode;
import com.cloud.console.security.WebUtils;
import com.nimbusds.jose.JWSObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {
    @Autowired
    private RedisTemplate redisTemplate;

    // 是否演示环境
    @Value("${demo}")
    private Boolean isDemoEnvironment;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 演示环境禁止删除和修改
        if (isDemoEnvironment &&
                HttpMethod.DELETE.toString().equals(request.getMethodValue())) {
            log.warn(ResultCode.FORBIDDEN_OPERATION.getMsg());
            return WebUtils.writeFailedToResponse(response, ResultCode.FORBIDDEN_OPERATION);
        }

        // 无token放行
        String token = exchange.getRequest().getHeaders().getFirst(Constants.JWT_TOKEN_HEADER);
        if (StrUtil.isBlank(token)) {
            return chain.filter(exchange);
        }

        // 解析JWT获取jti，以jti为key判断redis的黑名单列表是否存在，存在拦截响应token失效
        token = token.replace(Constants.JWT_TOKEN_PREFIX, Strings.EMPTY);
        JWSObject jwsObject = JWSObject.parse(token);
        String payload = jwsObject.getPayload().toString();
        JSONObject jsonObject = JSONUtil.parseObj(payload);
        String jti = jsonObject.getStr(Constants.JWT_JTI_KEY);
        Boolean isBlack = redisTemplate.hasKey(Constants.TOKEN_BLACKLIST_PREFIX + jti);
        if (isBlack) {
            return WebUtils.writeFailedToResponse(response, ResultCode.TOKEN_INVALID_OR_EXPIRED);
        }

        // 存在token且不是黑名单，request写入JWT的载体信息
        request = exchange.getRequest().mutate()
                .header(Constants.JWT_PAYLOAD_KEY, payload)
                .build();
        exchange = exchange.mutate().request(request).build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
