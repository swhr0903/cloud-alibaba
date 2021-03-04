package com.cloud.console.filter;

import com.cloud.console.ribbon.RibbonFilterContext;
import com.cloud.console.ribbon.RibbonFilterContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Component
@Slf4j
public class WeightRoundRibonFilter implements GlobalFilter, Ordered {

    @Resource
    private final LoadBalancerClientFactory clientFactory;

    @Resource
    private LoadBalancerProperties properties;

    public WeightRoundRibonFilter(LoadBalancerClientFactory clientFactory, LoadBalancerProperties properties) {
        this.clientFactory = clientFactory;
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //System.out.println(Thread.currentThread().getName());
        String version = exchange.getRequest().getHeaders().getFirst("version");
        if (StringUtils.isNotBlank(version)) {
            RibbonFilterContext currentContext = RibbonFilterContextHolder.getCurrentContext();
            currentContext.add("version", version);
        }
        Mono<Void> mono = chain.filter(exchange).subscriberContext(ctx -> ctx.put("version", version));
        return mono;
    }

    @Override
    public int getOrder() {
        return 10000;
    }
}
