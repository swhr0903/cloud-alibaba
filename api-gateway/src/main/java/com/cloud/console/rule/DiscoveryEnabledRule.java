package com.cloud.console.rule;

import com.cloud.console.predicate.DiscoveryEnabledPredicate;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.AvailabilityPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.PredicateBasedRule;
import org.springframework.util.Assert;

public abstract class DiscoveryEnabledRule extends PredicateBasedRule {

    private final DiscoveryEnabledPredicate predicate;


    public DiscoveryEnabledRule(DiscoveryEnabledPredicate discoveryEnabledPredicate) {
        Assert.notNull(discoveryEnabledPredicate, "Parameter 'discoveryEnabledPredicate' can't be null");
//        this.predicate = createCompositePredicate(discoveryEnabledPredicate, new AvailabilityPredicate(this, null));
        this.predicate = discoveryEnabledPredicate;
    }

    @Override
    public AbstractServerPredicate getPredicate() {
        return predicate;
    }

    private CompositePredicate createCompositePredicate(DiscoveryEnabledPredicate discoveryEnabledPredicate, AvailabilityPredicate availabilityPredicate) {
        return CompositePredicate.withPredicates(discoveryEnabledPredicate, availabilityPredicate)
                .build();
    }
}
