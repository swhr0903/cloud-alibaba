package com.cloud.console.predicate;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.cloud.console.ribbon.NacosWeightRoundRobin;
import com.cloud.console.ribbon.RibbonFilterContext;
import com.cloud.console.ribbon.RibbonFilterContextHolder;
import com.cloud.console.ribbon.WeightRoundRobin;
import com.google.common.base.Optional;
import com.netflix.loadbalancer.Server;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NacosMetadataAwarePredicate extends DiscoveryEnabledPredicate {
    WeightRoundRobin weightRoundRobin = new NacosWeightRoundRobin();

    @Override
    protected boolean apply(Server server) {

//        Mono.subscriberContext()
//                .map(ctx -> (String)ctx.get(VERSION))
//                .subscribe(e->{
//                    System.out.println(e);
//                });
//

        System.out.println(Thread.currentThread().getName());
        final RibbonFilterContext context = RibbonFilterContextHolder.getCurrentContext();
        final Set<Map.Entry<String, String>> attributes = Collections.unmodifiableSet(context.getAttributes().entrySet());
        final Map<String, String> metadata = ((NacosServer) server).getMetadata();

        boolean b = metadata.entrySet().containsAll(attributes);
        if (!b) {
            System.out.println("不满足条件");
            System.out.println("-------- 需要的meta信息 --------");
            attributes.forEach(e -> System.out.println(e.getKey() + ":" + e.getValue()));
            System.out.println("-------- 存在的meta信息 --------" + server.getPort());
            metadata.forEach((k, v) -> System.out.println(k + ":" + v));
        }
        return b;
    }

    @Override
    public Optional<Server> chooseRoundRobinAfterFiltering(List<Server> servers, Object loadBalancerKey) {
        List<Server> eligible = getEligibleServers(servers, loadBalancerKey);
        if (eligible.size() == 0) {
            return Optional.absent();
        }
        return Optional.of(weightRoundRobin.choose(eligible));
    }
}
