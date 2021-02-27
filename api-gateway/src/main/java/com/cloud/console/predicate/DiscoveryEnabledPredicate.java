package com.cloud.console.predicate;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.loadbalancer.Server;
import org.springframework.lang.Nullable;

public abstract class DiscoveryEnabledPredicate extends AbstractServerPredicate {
    @Override
    public boolean apply(@Nullable PredicateKey input) {
        return input != null
                && apply(input.getServer());
    }

    protected abstract boolean apply(Server server);
}
